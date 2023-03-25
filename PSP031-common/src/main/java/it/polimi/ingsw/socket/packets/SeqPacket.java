package it.polimi.ingsw.socket.packets;

import java.io.Serializable;

/**
 * Record that wraps a packet, adding a sequence number
 * This is the only object that can actually be sent over sockets
 * 
 * @param packet packet to wrap
 * @param seqN sequence number
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
public record SeqPacket(Packet packet, long seqN) implements Serializable {
    @Override
    public String toString() {
        return "SeqPacket{" +
                "packet=" + packet +
                ", seqN=" + seqN +
                '}';
    }
}
