package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Handle socket communication from multiple threads, using a queue.
 * Allows sending acks and replies.
 *
 * @param <IN> type of Packet that can be received
 * @param <ACK_IN> type of AckPacket that can be received
 * @param <ACK_OUT> type of AckPacket that can be sent
 * @param <OUT> type of Packet that can be sent
 */
public interface SocketManager<IN extends Packet, ACK_IN extends /* Packet & */ AckPacket, ACK_OUT extends /* Packet & */ AckPacket, OUT extends Packet>
        extends Closeable {

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
     * @throws IOException if sending and/or receiving fails
     * @throws InterruptedIOException if interrupted while sending and/or receiving
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
     * @throws IOException if sending fails
     * @throws InterruptedIOException if interrupted while receiving
     */
    <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> send(OUT p, Class<R> replyType) throws IOException;

    /**
     * Wait for a packet of the given type
     *
     * @param type type of the Packet to wait for
     * @return {@link PacketReplyContext} the context of the received packet
     * @throws IOException if receiving fails
     * @throws InterruptedIOException if interrupted while receiving
     */
    <R extends IN> PacketReplyContext<ACK_IN, ACK_OUT, R> receive(Class<R> type) throws IOException;

    void setNick(String nick);

    boolean isClosed();

    /**
     * Hook which allows doing cleanup before and after the SocketManager is closed
     * 
     * @param onClose hook which will be called on socket close.
     *        The actual SocketManager close method will be passed as a parameter and the hook itself
     *        will be in charge of calling it at what it deems to be the right time.
     */
    void setOnClose(@Nullable OnCloseHook onClose);

    /** @see #setOnClose(OnCloseHook) */
    @FunctionalInterface
    interface OnCloseHook {

        void doClose(Closeable socketManagerDoClose) throws IOException;
    }

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
         * @throws IOException if sending fails
         * @throws InterruptedIOException if interrupted while sending
         */
        void ack() throws IOException;

        /**
         * Replies with a simple ack packet, as {@link #ack()} would do, only
         * if none were already sent
         *
         * @throws IOException if sending fails
         * @throws InterruptedIOException if interrupted while sending
         */
        @Override
        void close() throws IOException;

        /**
         * Reply with the given packet
         * This will also acknowledge the current packet
         *
         * @param p packet to send
         * @throws IOException if sending and/or receiving fails
         * @throws InterruptedIOException if interrupted while sending and/or receiving
         */
        void reply(ACK_OUT p) throws IOException;

        /**
         * Reply with the given packet
         * This will also acknowledge the current packet
         *
         * @param p packet to send
         * @param replyType type of the AckPacket to wait for
         * @return context of the received AckPacket
         * @throws IOException if sending and/or receiving fails
         * @throws InterruptedIOException if interrupted while sending and/or receiving
         */
        <R extends ACK_IN> PacketReplyContext<ACK_IN, ACK_OUT, R> reply(ACK_OUT p, Class<R> replyType) throws IOException;
    }
}
