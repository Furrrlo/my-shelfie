package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class SocketManagerImpl<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        implements SocketManager<IN, ACK_IN, ACK_OUT, OUT> {

    @VisibleForTesting
    static final String CLOSE_EX_MSG = "Socket was closed";

    private volatile boolean isClosed;
    private final @Nullable Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    /** Maximum time to wait for a receive operation in {@link #defaultRecvTimeoutUnit}, or -1 to wait indefinitely */
    private final long defaultRecvTimeout;
    private final TimeUnit defaultRecvTimeoutUnit;
    private final BlockingDeque<QueuedOutput> outPacketQueue = new LinkedBlockingDeque<>();
    private final NBlockingQueue<SeqPacket> inPacketQueue = new NBlockingQueue<>();

    private final Future<?> recvTask;
    private final Future<?> sendTask;

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
                             long defaultRecvTimeout,
                             TimeUnit defaultRecvTimeoutUnit)
            throws IOException {
        this(name, executor, socket, socket.getInputStream(), socket.getOutputStream(), defaultRecvTimeout,
                defaultRecvTimeoutUnit);
    }

    @VisibleForTesting
    SocketManagerImpl(String name,
                      ExecutorService executor,
                      InputStream is,
                      OutputStream os,
                      long defaultRecvTimeout,
                      TimeUnit defaultRecvTimeoutUnit)
            throws IOException {
        this(name, executor, null, is, os, defaultRecvTimeout, defaultRecvTimeoutUnit);
    }

    private SocketManagerImpl(String name,
                              ExecutorService executor,
                              @Nullable Socket socket,
                              InputStream is,
                              OutputStream os,
                              long defaultRecvTimeout,
                              TimeUnit defaultRecvTimeoutUnit)
            throws IOException {
        this.socket = socket;
        this.name = name;
        this.oos = os instanceof ObjectOutputStream oos ? oos : new ObjectOutputStream(os);
        this.ois = is instanceof ObjectInputStream ois ? ois : new ObjectInputStream(is);
        this.defaultRecvTimeout = defaultRecvTimeout;
        this.defaultRecvTimeoutUnit = defaultRecvTimeoutUnit;

        recvTask = executor.submit(this::readLoop);
        sendTask = executor.submit(this::writeLoop);
    }

    @Override
    public void close() throws IOException {
        log("Closing socket manager...");
        isClosed = true;

        recvTask.cancel(true);
        sendTask.cancel(true);

        final IOException closeEx = new IOException(CLOSE_EX_MSG);
        outPacketQueue.forEach(q -> q.future().completeExceptionally(closeEx));
        outPacketQueue.clear();

        // Use a try-with-resources so everything is closed even if any of the close methods fail
        try (var ignoredOos = this.oos;
             var ignoredOis = this.ois) {
            if (socket != null)
                socket.close();
        }
    }

    private void ensureOpen() throws IOException {
        if (isClosed)
            throw new IOException(CLOSE_EX_MSG);
    }

    private void readLoop() {
        try {
            do {
                SeqPacket p;
                try {
                    p = (SeqPacket) ois.readObject();
                } catch (ClassNotFoundException | ClassCastException ex) {
                    log("Received unexpected input packet");
                    ex.printStackTrace();
                    continue;
                }

                log("Received packet: " + p);
                inPacketQueue.add(p);
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException | ClosedByInterruptException e) {
            // Go on, interruption is expected
        } catch (IOException e) {
            // If the interruption flag was set, we got interrupted by close, so it's expected
            if (Thread.currentThread().isInterrupted())
                return;

            log("Failed to read packet, closing...");
            e.printStackTrace();
            try {
                close();
            } catch (IOException ignored) {
                // Ignore
            }
        }
    }

    private void writeLoop() {
        try {
            do {
                QueuedOutput p = outPacketQueue.take();

                try {
                    oos.writeObject(p.packet());
                    log("Sent " + p);
                    p.future().complete(null);
                } catch (InvalidClassException | NotSerializableException ex) {
                    p.future().completeExceptionally(ex);
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException | ClosedByInterruptException | InterruptedException e) {
            // Go on, interruption is expected
        } catch (IOException e) {
            // If the interruption flag was set, we got interrupted by close, so it's expected
            if (Thread.currentThread().isInterrupted())
                return;

            log("Failed to write packet, closing...");
            e.printStackTrace();
            try {
                close();
            } catch (IOException ignored) {
                // Ignore
            }
        }
    }

    private CompletableFuture<Void> doSend(SeqPacket toSend) throws IOException {
        ensureOpen();

        final CompletableFuture<Void> hasSent = new CompletableFuture<>();
        log("Sending " + toSend + "...");
        outPacketQueue.add(new QueuedOutput(toSend, hasSent));
        System.out.println(outPacketQueue.size());
        return hasSent;
    }

    private SeqPacket doReceive(Predicate<SeqPacket> filter) throws InterruptedException, IOException, TimeoutException {
        ensureOpen();
        // TODO: this will wait indefinitely when the socket closes, is there any way to fail this?
        return defaultRecvTimeout == -1
                ? inPacketQueue.takeFirstMatching(filter)
                : inPacketQueue.takeFirstMatching(filter, defaultRecvTimeout, defaultRecvTimeoutUnit);
    }

    private <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> doSendAndWaitResponse(SeqPacket p, Class<R> replyType)
            throws IOException {
        try {
            final long seqN = p.seqN();
            doSend(p).get();
            log("Waiting for  " + replyType + "...");
            return new PacketReplyContextImpl<>(doReceive(packet -> replyType.isInstance(packet.packet()) &&
                    packet instanceof SeqAckPacket ack &&
                    ack.seqAck() == seqN));
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof IOException)
                throw new IOException("Failed to send packet " + p, e);

            throw new RuntimeException("Failed to send packet " + p, e);
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
            throw new RuntimeException("Failed to receive packet " + type, e);
        } catch (TimeoutException e) {
            throw new IOException("Timeout expired while waiting to receive packet " + type, e);
        }
    }

    @Override
    public void setNick(String nick) {
        this.nick = nick;
    }

    private void log(String s) {
        System.out.println("[" + name + "][" + nick + "] " + s);
    }

    private class PacketReplyContextImpl<T extends Packet> implements PacketReplyContext<ACK_IN, ACK_OUT, T> {

        private final SeqPacket packet;

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
            try {
                doSend(new SeqAckPacket(new SimpleAckPacket(), -1, packet.seqN())).get();
            } catch (InterruptedException | ExecutionException e) {
                if (e.getCause() instanceof IOException)
                    throw new IOException("Failed to ack packet " + packet, e);

                throw new RuntimeException("Failed to ack packet " + packet, e);
            }
        }

        @Override
        @SuppressWarnings({ "unchecked", "resource" }) // We don't care about acknowledging a SimpleAckPacket
        public void reply(ACK_OUT p) throws IOException {
            reply(p, (Class<ACK_IN>) SimpleAckPacket.class);
        }

        @Override
        public <R1 extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R1> reply(ACK_OUT p, Class<R1> replyType)
                throws IOException {
            long seqN = seq.getAndIncrement();
            return doSendAndWaitResponse(new SeqAckPacket(p, seqN, packet.seqN()), replyType);
        }
    }
}