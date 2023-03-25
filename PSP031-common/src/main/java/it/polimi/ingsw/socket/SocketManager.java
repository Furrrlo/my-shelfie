package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;

import java.io.Closeable;
import java.io.IOException;

public interface SocketManager<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet> {

    <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException;

    <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException;

    void setNick(String nick);

    interface PacketReplyContext<ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, T extends Packet>
            extends Closeable {

        T getPacket();

        void ack() throws IOException;

        @Override
        default void close() throws IOException {
            ack();
        }

        <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> reply(ACK_OUT p, Class<R> replyType) throws IOException;
    }
}
