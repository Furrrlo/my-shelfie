package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.socket.SocketManager;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

public interface ServerSocketManager extends SocketManager<C2SPacket, C2SAckPacket, S2CAckPacket, S2CPacket> {
}
