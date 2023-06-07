package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.Packet;

/**
 * Record that wraps a packet, adding a sequence number
 * 
 * @param packet packet to wrap
 * @param seqN sequence number
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
record SimpleSeqPacket(Packet packet, long seqN) implements SeqPacket {
}
