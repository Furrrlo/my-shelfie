package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import it.polimi.ingsw.socket.packets.SeqPacket;
import it.polimi.ingsw.socket.packets.SimpleAckPacket;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class SocketManagerImpl<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        implements SocketManager<IN, ACK_IN, ACK_OUT, OUT> {

    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final BlockingDeque<QueuedOutput> outPacketQueue;
    private final List<QueuedInput> inQueue = new CopyOnWriteArrayList<>();

    private final Thread recvThread;
    private final Thread sendThread;

    private final AtomicLong seq;
    private final String name;
    private String nick;

    record QueuedInput(Predicate<SeqPacket> filter, CompletableFuture<SeqPacket> future) {
    }

    record QueuedOutput(SeqPacket packet, CompletableFuture<Void> future) {
    }

    public SocketManagerImpl(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        nick = "";
        outPacketQueue = new LinkedBlockingDeque<>();
        seq = new AtomicLong();
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        recvThread = new Thread(this::readLoop);
        recvThread.setName(name + "SocketManagerImpl-recv-thread");
        recvThread.start();

        sendThread = new Thread(this::writeLoop);
        sendThread.setName(name + "SocketManagerImpl-send-thread");
        sendThread.start();
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

                log("Waiting for: " + inQueue.stream().filter(c -> !c.future.isDone()).count());
                if (!inQueue.isEmpty())
                    log("Accepted: " + inQueue.get(0).filter.test(p));
                log("Received packet: " + p);

                final var maybeReceiver = inQueue.stream()
                        .filter(c -> c.filter.test(p))
                        .findFirst();
                if (maybeReceiver.isPresent()) {
                    QueuedInput receiver = maybeReceiver.get();
                    receiver.future().complete(p);
                    inQueue.remove(receiver);
                } else {
                    // TODO: what can we do here?
                    log("WARN: No receiver found, discarding packet " + p);
                }

                log("Now waiting for: " + inQueue.stream().filter(c -> !c.future.isDone()).count());
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

    private CompletableFuture<SeqPacket> doReceive(Predicate<SeqPacket> filter) {
        CompletableFuture<SeqPacket> toReceive = new CompletableFuture<>();
        inQueue.add(new QueuedInput(filter, toReceive));
        return toReceive;
    }

    @Override
    @SuppressWarnings({ "unchecked", "resource" }) // We don't care about acknowledging a SimpleAckPacket
    public void send(OUT p) throws IOException {
        send(p, (Class<ACK_IN>) SimpleAckPacket.class);
    }

    @Override
    public <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException {
        try {
            long seqN = seq.getAndIncrement();
            CompletableFuture<SeqPacket> toReceive = doReceive(
                    packet -> replyType.isInstance(packet.packet()) && ((AckPacket) packet.packet()).seqAck() == seqN);
            doSend(new SeqPacket(p, seqN)).get();
            log("Waiting for  " + replyType + "...");
            return new PacketReplyContextImpl<>(toReceive.get());
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof IOException)
                throw new IOException("Failed to send packet " + p, e);

            throw new RuntimeException("Failed to send packet " + p, e);
        }
    }

    @Override
    public <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException {
        CompletableFuture<SeqPacket> toReceive = doReceive(packet -> type.isInstance(packet.packet()));
        try {
            log("Waiting for  " + type + "...");
            return new PacketReplyContextImpl<>(toReceive.get());
        } catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof IOException)
                throw new IOException("Failed to receive packet " + type, e);

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
                doSend(new SeqPacket(new SimpleAckPacket(packet.seqN()), -1)).get();
            } catch (InterruptedException | ExecutionException e) {
                if (e.getCause() instanceof IOException)
                    throw new IOException("Failed to ack packet " + packet, e);

                throw new RuntimeException("Failed to ack packet " + packet, e);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void reply(ACK_OUT p) throws IOException {
            send((OUT) p);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R1 extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R1> reply(ACK_OUT p, Class<R1> replyType)
                throws IOException {
            p.setSeqAck(packet.seqN());
            return send((OUT) p, replyType);
        }
    }
}