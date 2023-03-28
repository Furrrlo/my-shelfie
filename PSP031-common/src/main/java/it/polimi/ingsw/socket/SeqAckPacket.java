package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;

/**
 * Record that wraps a packet, adding a sequence number
 * This is the only object that can actually be sent over sockets
 * 
 * @param packet packet to wrap
 * @param seqN sequence number
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
record SeqAckPacket(AckPacket packet, long seqN, long seqAck) implements SeqPacket {
}
