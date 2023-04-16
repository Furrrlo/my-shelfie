package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.socket.SocketManagerImpl;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerSocketManagerImpl
        extends SocketManagerImpl<C2SPacket, C2SAckPacket, S2CAckPacket, S2CPacket>
        implements ServerSocketManager {

    public ServerSocketManagerImpl(ExecutorService executor, Socket socket) throws IOException {
        super("Server", executor, socket);
    }

    public ServerSocketManagerImpl(ExecutorService executor,
                                   Socket socket,
                                   long defaultResponseTimeout,
                                   TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        super("Server", executor, socket, defaultResponseTimeout, defaultResponseTimeoutUnit);
    }
}
