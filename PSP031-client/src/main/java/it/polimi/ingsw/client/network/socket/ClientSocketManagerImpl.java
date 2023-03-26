package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.socket.SocketManagerImpl;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

import java.io.IOException;
import java.net.Socket;

public class ClientSocketManagerImpl
        extends SocketManagerImpl<S2CPacket, S2CAckPacket, C2SAckPacket, C2SPacket>
        implements ClientSocketManager {

    public ClientSocketManagerImpl(Socket socket) throws IOException {
        super(socket, "Client");
    }
}
