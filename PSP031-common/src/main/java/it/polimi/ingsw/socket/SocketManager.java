package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;

import java.io.Closeable;
import java.io.IOException;

/**
 * Handle socket communication from multiple threads, using a queue.
 * Allows sending acks and replies.
 *
 * @param <IN> type of Packet that can be received
 * @param <ACK_IN> type of AckPacket that can be received
 * @param <ACK_OUT> type of AckPacket that can be sent
 * @param <OUT> type of Packet that can be sent
 */
public interface SocketManager<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet> {

    /**
     * Send a packet and wait for an ack.
     * The given packet is wrapped in a {@link SimpleSeqPacket} and the
     * {@link SimpleSeqPacket#seqN()} is set.
     * The packet is added to a queue to be sent.
     * Wait for a {@link SimpleAckPacket} with {@link SeqAckPacket#seqAck()} ==
     * {@link SimpleSeqPacket#seqN()}
     *
     *
     * @param p packet to be sent
     * @throws IOException
     */
    void send(OUT p) throws IOException;

    /**
     * Send a packet and wait for an ack.
     * The given packet is wrapped in a {@link SimpleSeqPacket} and the
     * {@link SimpleSeqPacket#seqN()} is set.
     * The packet is added to a queue to be sent.
     * Wait for an AckPacket of the given type and with {@link SeqAckPacket#seqAck()} ==
     * {@link SimpleSeqPacket#seqN()}
     *
     *
     * @param p packet to be sent
     * @param replyType type of the AckPacket to wait for
     * @return {@link PacketReplyContext} the context of the received ack
     * @throws IOException
     */
    <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException;

    /**
     * Wait for a packet of the given type
     *
     * @param type type of the Packet to wait for
     * @return {@link PacketReplyContext} the context of the received packet
     * @throws IOException
     */
    <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException;

    void setNick(String nick);

    /**
     * Context of a Packet
     * Wraps a {@link SimpleSeqPacket} and allows to ack and reply
     *
     * @param <ACK_IN> type of AckPacket that can be received
     * @param <ACK_OUT> type of AckPacket that can be sent
     * @param <T> type of Packet
     */
    interface PacketReplyContext<ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, T extends Packet>
            extends Closeable {

        /**
         * Extract the {@link Packet} from the {@link SimpleSeqPacket} wrapped in this context
         *
         * @return the packet contained in the {@link SimpleSeqPacket}
         */
        T getPacket();

        /**
         * Send a {@link SimpleAckPacket} with the {@link SeqAckPacket#seqAck()} of this packet
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
         * This will also acknowledge the current packet
         *
         * @param p packet to send
         * @throws IOException
         */
        void reply(ACK_OUT p) throws IOException;

        /**
         * Reply with the given packet
         * This will also acknowledge the current packet
         *
         * @param p packet to send
         * @param replyType type of the AckPacket to wait for
         * @return context of the received AckPacket
         * @throws IOException
         */
        <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> reply(ACK_OUT p, Class<R> replyType) throws IOException;
    }
}
