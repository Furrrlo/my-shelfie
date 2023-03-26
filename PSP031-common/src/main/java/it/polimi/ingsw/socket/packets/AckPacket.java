package it.polimi.ingsw.socket.packets;

/**
 * Packet with the sequence number of the packet to acknowledge
 * This will also be wrapped in a {@link SeqAckPacket}
 */
public interface AckPacket extends Packet {
}
