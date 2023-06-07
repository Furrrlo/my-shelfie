package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;

/**
 * Record that wraps a packet, adding a sequence number and acknowledging a previous packet
 * 
 * @param packet packet to wrap
 * @param seqN sequence number
 * @param seqAck sequence number of the packet to be ack-ed
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
record SeqAckPacket(AckPacket packet, long seqN, long seqAck) implements SeqPacket {
}
