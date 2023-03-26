package it.polimi.ingsw.socket.packets;

import java.io.Serializable;

/**
 * Interface that wraps a packet and adds a sequence number
 * Its implementors are the only object that can actually be sent over sockets
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
public sealed interface SeqPacket extends Serializable permits SimpleSeqPacket,SeqAckPacket {

    /** Returns the wrapped packet */
    Packet packet();

    /** Returns the sequence number */
    long seqN();
}
