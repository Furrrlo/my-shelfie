package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.socket.SocketManager;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

/**
 * Server-specific SocketManager interface in charge of specifying the flow of packets
 * expected by the server
 *
 * @see SocketManager
 */
public interface ServerSocketManager extends SocketManager<C2SPacket, C2SAckPacket, S2CAckPacket, S2CPacket> {
}
