package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class SocketManagerImpl<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        implements SocketManager<IN, ACK_IN, ACK_OUT, OUT> {

    private final @Nullable Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final BlockingDeque<QueuedOutput> outPacketQueue = new LinkedBlockingDeque<>();
    private final NBlockingQueue<SeqPacket> inPacketQueue = new NBlockingQueue<>();

    private final Future<?> recvTask;
    private final Future<?> sendTask;

    private final AtomicLong seq = new AtomicLong();
    private final String name;
    private String nick = "";

    record QueuedOutput(SeqPacket packet, CompletableFuture<Void> future) {
    }

    public SocketManagerImpl(String name, ExecutorService executor, Socket socket) throws IOException {
        this(name, executor, socket, socket.getInputStream(), socket.getOutputStream());
    }

    @VisibleForTesting
    SocketManagerImpl(String name, ExecutorService executor, InputStream is, OutputStream os) throws IOException {
        this(name, executor, null, is, os);
    }

    private SocketManagerImpl(String name,
                              ExecutorService executor,
                              @Nullable Socket socket,
                              InputStream is,
                              OutputStream os)
            throws IOException {
        this.socket = socket;
        this.name = name;
        this.oos = os instanceof ObjectOutputStream oos ? oos : new ObjectOutputStream(os);
        this.ois = is instanceof ObjectInputStream ois ? ois : new ObjectInputStream(is);

        recvTask = executor.submit(this::readLoop);
        sendTask = executor.submit(this::writeLoop);
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
        } catch (IOException e) {
            // TODO: close socket
            throw new UncheckedIOException(e);
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
        } catch (IOException e) {
            // TODO: close socket
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private CompletableFuture<Void> doSend(SeqPacket toSend) {
        final CompletableFuture<Void> hasSent = new CompletableFuture<>();
        log("Sending " + toSend + "...");
        outPacketQueue.add(new QueuedOutput(toSend, hasSent));
        System.out.println(outPacketQueue.size());
        return hasSent;
    }

    private SeqPacket doReceive(Predicate<SeqPacket> filter) throws InterruptedException {
        return inPacketQueue.takeFirstMatching(filter);
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
    public <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) {
        try {
            log("Waiting for  " + type + "...");
            return new PacketReplyContextImpl<>(doReceive(packet -> type.isInstance(packet.packet())));
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to receive packet " + type, e);
        }
    }

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