package it.polimi.ingsw.socket.packets;

/**
 * Empty ack packet with only the sequence number of the packet to acknowledge
 * This will also be wrapped in a {@link SeqAckPacket}, but it's seqN is not used.
 *
 * @see it.polimi.ingsw.socket.SocketManager.PacketReplyContext#ack()
 */
public record SimpleAckPacket() implements S2CAckPacket, C2SAckPacket {
}
