package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import it.polimi.ingsw.utils.ThreadPools;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class SocketManagerImpl<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        implements SocketManager<IN, ACK_IN, ACK_OUT, OUT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketManagerImpl.class);
    @VisibleForTesting
    static final String CLOSE_EX_MSG = "Socket was closed";

    private final AtomicBoolean isClosing = new AtomicBoolean();
    private volatile boolean isClosed;
    private volatile @Nullable OnCloseHook onClose;
    private volatile @Nullable SeqPacket closePacket;
    private final @Nullable Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    /** Maximum time to wait for a response in {@link #defaultResponseTimeoutUnit}, or -1 to wait indefinitely */
    protected final long defaultResponseTimeout;
    protected final TimeUnit defaultResponseTimeoutUnit;
    private final BlockingDeque<QueuedOutput> outPacketQueue = new LinkedBlockingDeque<>();
    private final NBlockingQueue<Object> inPacketQueue = new NBlockingQueue<>();

    private final Future<?> recvTask;
    private volatile boolean isRecvTaskRunning;
    private final Future<?> sendTask;
    private volatile boolean isSendTaskRunning;

    private final AtomicLong seq = new AtomicLong();
    private final String name;
    private String nick = "";

    record QueuedOutput(SeqPacket packet, CompletableFuture<Void> future) {
    }

    public SocketManagerImpl(String name,
                             ExecutorService executor,
                             Socket socket)
            throws IOException {
        this(name, executor, socket, socket.getInputStream(), socket.getOutputStream(), -1, TimeUnit.MILLISECONDS);
    }

    public SocketManagerImpl(String name,
                             ExecutorService executor,
                             Socket socket,
                             long defaultResponseTimeout,
                             TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        this(name, executor, socket, socket.getInputStream(), socket.getOutputStream(), defaultResponseTimeout,
                defaultResponseTimeoutUnit);
    }

    @VisibleForTesting
    SocketManagerImpl(String name,
                      ExecutorService executor,
                      InputStream is,
                      OutputStream os,
                      long defaultResponseTimeout,
                      TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        this(name, executor, null, is, os, defaultResponseTimeout, defaultResponseTimeoutUnit);
    }

    private SocketManagerImpl(String name,
                              ExecutorService executor,
                              @Nullable Socket socket,
                              InputStream is,
                              OutputStream os,
                              long defaultResponseTimeout,
                              TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        this.socket = socket;
        this.name = name;
        this.oos = os instanceof ObjectOutputStream oos ? oos : new ObjectOutputStream(os);
        this.ois = is instanceof ObjectInputStream ois ? ois : new ObjectInputStream(is);
        this.defaultResponseTimeout = defaultResponseTimeout;
        this.defaultResponseTimeoutUnit = defaultResponseTimeoutUnit;

        recvTask = executor.submit(ThreadPools.giveNameToTask(n -> n + "[socket-recv]", this::readLoop));
        isRecvTaskRunning = true;
        sendTask = executor.submit(ThreadPools.giveNameToTask(n -> n + "[socket-send]", this::writeLoop));
        isSendTaskRunning = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public final void close() throws IOException {
        var onClose = this.onClose;
        if (onClose != null)
            onClose.doClose(this::doClose);
        else
            doClose();
    }

    @MustBeInvokedByOverriders
    @SuppressWarnings({
            "unchecked", // ClosePacket and CloseAckPacket need to be hard-casted
            "resource" // We don't need to ack the last CloseAckPacket
    })
    protected void doClose() throws IOException {
        if (isClosing.getAndSet(true))
            return;

        log("Closing socket manager...");

        try {
            var closePacket = this.closePacket;
            if (closePacket == null) {
                send((OUT) new ClosePacket(), (Class<ACK_IN>) CloseAckPacket.class);
            } else {
                doSend(new SeqAckPacket(new CloseAckPacket(), -1, closePacket.seqN()));
            }
        } catch (IOException ex) {
            // We ignore exceptions on the last ack receival, because the socket may
            // be closed before we are able to read out the last ack packet
            // We don't care about whether the other has received this anyway,
            // we can just hope it did and go on
        }

        isClosed = true;
        recvTask.cancel(true);
        sendTask.cancel(true);

        if (socket != null) {
            // This should take care of closing the in/out streams
            socket.close();
        } else {
            // Use a try-with-resources so everything is closed even if any of the close methods fail
            //noinspection EmptyTryBlock
            try (var ignoredOos = this.oos; var ignoredOis = this.ois) {
                // Empty body just to close
            }
        }
    }

    private boolean isClosePacket(@Nullable Packet packet) {
        return packet instanceof ClosePacket || packet instanceof CloseAckPacket;
    }

    @Override
    public void setOnClose(@Nullable OnCloseHook onClose) {
        this.onClose = onClose;
    }

    private void ensureOpen() throws IOException {
        if (isClosed)
            throw new IOException(CLOSE_EX_MSG);
    }

    private void readLoop() {
        try {
            SeqPacket closePacket = null;
            boolean wasClosed = false;
            do {
                SeqPacket p;
                try {
                    p = (SeqPacket) ois.readObject();
                } catch (ClassNotFoundException | ClassCastException ex) {
                    LOGGER.error("[{}][{}] Received unexpected input packet", name, nick, ex);
                    continue;
                }

                // Also read a null-object to make sure we received the reset from the corresponding ObjectOutputStream.
                // In particular, we need to read an object cause ObjectInputStream only handles reset requests in
                // readObject/readUnshared, not in readByte.
                // We use a null reference 'cause it's the smallest object I can think of sending.
                // By doing this, we make sure that by the time the current packet we are reading is handled, the
                // other side has already flushed out all its data related to this packet (including the reset req),
                // therefore we can (and some packets do) close the connection and the other side could do the same.
                Object resetFlushObj;
                try {
                    resetFlushObj = ois.readUnshared();
                    if (resetFlushObj != null)
                        throw new IOException("Received unexpected resetFlushObj " + resetFlushObj);
                } catch (ClassNotFoundException | ClassCastException ex) {
                    throw new IOException("Received unexpected resetFlushObj", ex);
                }

                log("Received packet: " + p);
                wasClosed = isClosePacket(p.packet());
                if (p.packet() instanceof ClosePacket) {
                    closePacket = p;
                } else {
                    inPacketQueue.add(p);
                }
            } while (!wasClosed && !Thread.currentThread().isInterrupted());

            if (closePacket != null) {
                // Set it here (other than in the finally-block) as we need it set before the close call
                this.isRecvTaskRunning = false;
                this.closePacket = closePacket;
                // Close the socket, close will be in charge of sending the ack
                try {
                    close();
                } catch (IOException ex) {
                    LOGGER.error("[{}][{}] Failed to close socket after close packet...", name, nick, ex);
                }
            }
        } catch (IOException e) {
            // Set it here (other than in the finally-block) as we need it set before the close call
            this.isRecvTaskRunning = false;

            // If it's an interrupted exception or the interruption flag was set
            final boolean isTimeout = e instanceof SocketTimeoutException;
            if (!isTimeout && (e instanceof InterruptedIOException
                    || e instanceof ClosedByInterruptException
                    || Thread.currentThread().isInterrupted()))
                return;

            LOGGER.error("[{}][{}] Failed to read packet, closing...", name, nick, e);
            try {
                close();
            } catch (IOException ignored) {
                // Ignore
            }
        } finally {
            this.isRecvTaskRunning = false;

            // Signal to everybody who is waiting that the socket got closed
            inPacketQueue.add(new IOException(CLOSE_EX_MSG));
        }
    }

    private void writeLoop() {
        QueuedOutput p = null;
        try {
            do {
                p = outPacketQueue.take();

                try {
                    oos.writeObject(p.packet());
                    // Fix memory leak, as ObjectOutputStream maintains a reference to anything
                    // you write into it, in order to implement the reference sharing mechanism.
                    // Since we don't need to share references past a single object graph, we
                    // can just reset the references after each time we write.
                    // see https://bugs.openjdk.org/browse/JDK-6525563
                    oos.reset();
                    // Write a null reference to use as a marker that the reset request was flushed
                    // and received with the packet by the other side.
                    // See the readLoop for additional details
                    oos.writeUnshared(null);
                    oos.flush();

                    log("Sent " + p);
                    p.future().complete(null);
                } catch (InvalidClassException | NotSerializableException ex) {
                    p.future().completeExceptionally(ex);
                } catch (Throwable ex) {
                    p.future().completeExceptionally(ex);
                    throw ex;
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException | ClosedByInterruptException | InterruptedException e) {
            // Go on, interruption is expected
        } catch (IOException e) {
            // Set it here (other than in the finally-block) as we need it set before the close call
            this.isSendTaskRunning = false;

            // If the interruption flag was set, we got interrupted by close, so it's expected
            if (Thread.currentThread().isInterrupted())
                return;
            // If it was a close packet being sent, we don't need to log the error and call close
            // see #doClose(...) for more details on the close sequence
            if (isClosePacket(p.packet().packet()))
                return;

            LOGGER.error("[{}][{}] Failed to write packet {}, closing...", name, nick, p, e);
            try {
                close();
            } catch (IOException ignored) {
                // Ignore
            }
        } finally {
            this.isSendTaskRunning = false;

            // Signal to everybody who is waiting that the socket got closed
            var toCancel = new ArrayList<>(outPacketQueue);
            outPacketQueue.removeAll(toCancel);
            final IOException closeEx = new IOException(CLOSE_EX_MSG);
            toCancel.forEach(q -> q.future().completeExceptionally(closeEx));
        }
    }

    private void doSend(SeqPacket toSend) throws IOException {
        ensureOpen();

        if (!isSendTaskRunning)
            throw new IOException(CLOSE_EX_MSG);

        final CompletableFuture<Void> hasSent = new CompletableFuture<>();
        log("Sending " + toSend + "...");
        outPacketQueue.add(new QueuedOutput(toSend, hasSent));
        log(String.valueOf(outPacketQueue.size()));

        try {
            ThreadPools.getUninterruptibly(hasSent);
        } catch (ExecutionException e) {
            throw rethrowIOException(e.getCause() != null ? e.getCause() : e, "Failed to send packet " + toSend);
        }
    }

    private SeqPacket doReceive(Predicate<SeqPacket> filter, long timeout, TimeUnit timeoutUnit)
            throws InterruptedException, IOException, TimeoutException {
        ensureOpen();

        if (!isRecvTaskRunning)
            throw new IOException(CLOSE_EX_MSG);

        final NBlockingQueue.Matcher<Object> cond = (obj, res) -> {
            // We should be the only ones getting this packet, consume it
            if (obj instanceof SeqPacket pkt && filter.test(pkt))
                return res.consume();
            // Exceptions are not specific to us, but to the whole receive thread, so
            // we shouldn't be consuming it, as everybody has to get it
            if (obj instanceof Throwable)
                return res.peek();

            return res.skip();
        };
        var res = timeout == -1
                ? inPacketQueue.takeFirstMatching(cond)
                : inPacketQueue.takeFirstMatching(cond, timeout, timeoutUnit);
        // Correct result
        if (res instanceof SeqPacket pkt)
            return pkt;
        // We got an exception
        if (res instanceof Throwable t)
            throw rethrowIOException(t, "Failed to receive packet");

        throw new AssertionError("Unexpected result from queue " + res);
    }

    private RuntimeException rethrowIOException(Throwable t, String msg) throws IOException {
        if (t instanceof RuntimeException ex) {
            ex.addSuppressed(new Exception("Called from here"));
            throw ex;
        }
        if (t instanceof Error ex) {
            ex.addSuppressed(new Exception("Called from here"));
            throw ex;
        }

        throw new IOException(msg, t);
    }

    private SeqPacket doReceive(Predicate<SeqPacket> filter) throws InterruptedException, IOException {
        try {
            return doReceive(filter, -1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            throw new AssertionError("Timeout expired where there should be no timeout", ex);
        }
    }

    private SeqPacket doReceiveWithTimeout(Predicate<SeqPacket> filter)
            throws InterruptedException, IOException, TimeoutException {
        return doReceive(filter, defaultResponseTimeout, defaultResponseTimeoutUnit);
    }

    private <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> doSendAndWaitResponse(SeqPacket p, Class<R> replyType)
            throws IOException {
        try {
            final long seqN = p.seqN();
            doSend(p);
            log("Waiting for  " + replyType + "...");
            return new PacketReplyContextImpl<>(doReceiveWithTimeout(packet -> replyType.isInstance(packet.packet()) &&
                    packet instanceof SeqAckPacket ack &&
                    ack.seqAck() == seqN));
        } catch (InterruptedException e) {
            throw (IOException) new InterruptedIOException("Failed to send packet " + p).initCause(e);
        } catch (TimeoutException e) {
            throw new IOException("Timeout expired while waiting for response packet " + replyType +
                    " after sending " + p, e);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "resource" }) // We don't care about acknowledging a SimpleAckPacket
    public void send(OUT p) throws IOException {
        send(p, (Class<ACK_IN>) SimpleAckPacket.class);
    }

    @Override
    public <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException {
        long seqN = seq.getAndIncrement();
        return doSendAndWaitResponse(new SimpleSeqPacket(p, seqN), replyType);
    }

    @Override
    public <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException {
        try {
            log("Waiting for  " + type + "...");
            return new PacketReplyContextImpl<>(doReceive(packet -> type.isInstance(packet.packet())));
        } catch (InterruptedException e) {
            throw (IOException) new InterruptedIOException("Failed to receive packet " + type).initCause(e);
        }
    }

    @Override
    public void setNick(String nick) {
        this.nick = nick;
    }

    private void log(String s) {
        LOGGER.trace("[{}][{}] {}", name, nick, s);
    }

    private class PacketReplyContextImpl<T extends Packet> implements PacketReplyContext<ACK_IN, ACK_OUT, T> {

        private final SeqPacket packet;
        private final AtomicBoolean hasAcked = new AtomicBoolean(false);

        PacketReplyContextImpl(SeqPacket packet) {
            this.packet = packet;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T getPacket() {
            return (T) packet.packet();
        }

        @Override
        public void ack() throws IOException {
            if (hasAcked.getAndSet(true))
                throw new IllegalStateException("Packet " + packet + " has already been acked");

            doAck();
        }

        @Override
        public void close() throws IOException {
            if (!hasAcked.getAndSet(true))
                doAck();
        }

        private void doAck() throws IOException {
            doSend(new SeqAckPacket(new SimpleAckPacket(), -1, packet.seqN()));
        }

        @Override
        @SuppressWarnings({ "unchecked", "resource" }) // We don't care about acknowledging a SimpleAckPacket
        public void reply(ACK_OUT p) throws IOException {
            reply(p, (Class<ACK_IN>) SimpleAckPacket.class);
        }

        @Override
        public <R1 extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R1> reply(ACK_OUT p, Class<R1> replyType)
                throws IOException {
            if (hasAcked.getAndSet(true))
                throw new IllegalStateException("Packet " + packet + " has already been acked");

            long seqN = seq.getAndIncrement();
            return doSendAndWaitResponse(new SeqAckPacket(p, seqN, packet.seqN()), replyType);
        }
    }
}