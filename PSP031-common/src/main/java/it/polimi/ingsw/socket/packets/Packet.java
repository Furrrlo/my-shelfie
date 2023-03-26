package it.polimi.ingsw.socket.packets;

import java.io.Serializable;

/**
 * Packet that can be sent over socket.
 *
 * @see it.polimi.ingsw.socket.SocketManager#send(Packet, Class)
 */
public interface Packet extends Serializable {
}
