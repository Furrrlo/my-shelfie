package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import it.polimi.ingsw.socket.packets.SeqPacket;
import it.polimi.ingsw.socket.packets.SimpleAckPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.*;
import java.util.function.Predicate;

public class SocketManagerImpl<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        implements SocketManager<IN, ACK_IN, ACK_OUT, OUT> {

    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final BlockingDeque<SeqPacket> outPacketQueue;
    private final List<QueuedInput> inQueue = new CopyOnWriteArrayList<>();

    private final ExecutorService threadPool;

    private final AtomicLong seq;
    private final String name;
    private String nick;

    record QueuedInput(Predicate<SeqPacket> filter, CompletableFuture<SeqPacket> future) {
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
        threadPool = Executors.newFixedThreadPool(2);
        threadPool.submit(() -> {
            do {
                try {
                    SeqPacket p = outPacketQueue.take();
                    oos.writeObject(p);
                    log("Sended " + p);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            } while (!Thread.currentThread().isInterrupted());
        });
        threadPool.submit(() -> {
            do {
                try {
                    SeqPacket p = (SeqPacket) ois.readObject();
                    log("Waiting for: " + inQueue.stream().filter(c -> !c.future.isDone()).count());
                    log("Accepted: " + inQueue.get(0).filter.test(p));
                    log("Received packet: " + p);
                    inQueue.stream()
                            .filter(c -> c.filter.test(p))
                            .findFirst()
                            .ifPresent(c -> c.future.complete(p));
                    log("Now waiting for: " + inQueue.stream().filter(c -> !c.future.isDone()).count());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } while (!Thread.currentThread().isInterrupted());
        });
    }

    @Override
    public <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException {
        try {
            CompletableFuture<SeqPacket> toReceive = new CompletableFuture<>();
            long seqN = seq.getAndIncrement();
            QueuedInput queuedInput = new QueuedInput(
                    packet -> replyType.isInstance(packet.packet()) && ((AckPacket) packet.packet()).seqAck() == seqN,
                    toReceive);
            inQueue.add(queuedInput);
            final SeqPacket toSend = new SeqPacket(p, seqN);
            log("Sending " + toSend + "...");
            outPacketQueue.put(toSend);
            System.out.println(outPacketQueue.size());
            log("Waiting for  " + replyType + "...");
            SeqPacket res = toReceive.get();
            inQueue.remove(queuedInput);
            return new PacketReplyContextImpl<>(res);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException {
        CompletableFuture<SeqPacket> toReceive = new CompletableFuture<>();
        QueuedInput queuedInput = new QueuedInput(packet -> type.isInstance(packet.packet()), toReceive);
        inQueue.add(queuedInput);
        log("Waiting for  " + type + "...");
        SeqPacket receivedPacket;
        try {
            receivedPacket = toReceive.get();
            inQueue.remove(queuedInput);
        } catch (ExecutionException | InterruptedException e) {
            throw new IOException(e);
        }
        return new PacketReplyContextImpl<>(receivedPacket);
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
        public T getPacket() {
            return (T) packet.packet();
        }

        @Override
        public void ack() throws IOException {
            final SeqPacket toSend = new SeqPacket(new SimpleAckPacket(packet.seqN()), -1);
            log("Sending " + toSend + "...");
            try {
                outPacketQueue.put(toSend);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <R1 extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R1> reply(ACK_OUT p, Class<R1> replyType)
                throws IOException {
            p.setSeqAck(packet.seqN());
            return send((OUT) p, replyType);
        }
    }
}