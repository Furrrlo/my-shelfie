package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.socket.SocketManager;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

public interface ClientSocketManager extends SocketManager<S2CPacket, S2CAckPacket, C2SAckPacket, C2SPacket> {
}