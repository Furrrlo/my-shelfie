package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;

/**
 * Empty ack packet with only the sequence number of the packet to acknowledge
 * This will also be wrapped in a {@link SeqAckPacket}, but it's seqN is not used.
 *
 * @see SocketManager.PacketReplyContext#ack()
 */
record SimpleAckPacket() implements S2CAckPacket, C2SAckPacket {
}
