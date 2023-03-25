package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.socket.SocketManagerImpl;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

import java.net.Socket;

public class ServerSocketManagerImpl
        extends SocketManagerImpl<C2SPacket, C2SAckPacket, S2CAckPacket, S2CPacket>
        implements ServerSocketManager {

    public ServerSocketManagerImpl(Socket socket) {
        super(socket, "Server");
    }
}
