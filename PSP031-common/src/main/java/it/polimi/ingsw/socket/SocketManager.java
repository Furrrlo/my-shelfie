package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;

import java.io.Closeable;
import java.io.IOException;

/**
 * @param <IN> type of Packet that can be received
 * @param <ACK_IN> type of AckPacket that can be received
 * @param <ACK_OUT> type of AckPacket that can be sent
 * @param <OUT> type of Packet that can be sent
 */
public interface SocketManager<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet> {

    /**
     * @param p packet to be sent
     * @param replyType type of the AckPacket to wait for
     * @return {@link PacketReplyContext} the context of the received ack
     * @throws IOException
     */
    <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException;

    /**
     * @param type type of the Packet to wait for
     * @return {@link PacketReplyContext} the context of the received packet
     * @throws IOException
     */
    <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException;

    void setNick(String nick);

    /**
     * Context of a Packet
     *
     * @param <ACK_IN> type of AckPacket that can be received
     * @param <ACK_OUT> type of AckPacket that can be sent
     * @param <T> type of Packet
     */
    interface PacketReplyContext<ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, T extends Packet>
            extends Closeable {

        T getPacket();

        /**
         * Send a {@link it.polimi.ingsw.socket.packets.SimpleAckPacket} with the
         * {@link it.polimi.ingsw.socket.packets.SimpleAckPacket#seqAck()} of this packet
         *
         * @throws IOException
         */
        void ack() throws IOException;

        /**
         * call {@link #ack()}
         *
         * @throws IOException
         */
        @Override
        default void close() throws IOException {
            ack();
        }

        /**
         * Reply with the given packet
         * This will also acknoledge the current packet
         *
         * @param p packet to send
         * @param replyType type of the AckPacket to wait for
         * @return context of the received AckPacket
         * @throws IOException
         */
        <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> reply(ACK_OUT p, Class<R> replyType) throws IOException;
    }
}
