package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.socket.SocketManagerImpl;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSocketManagerImpl
        extends SocketManagerImpl<S2CPacket, S2CAckPacket, C2SAckPacket, C2SPacket>
        implements ClientSocketManager {

    public ClientSocketManagerImpl(Socket socket) throws IOException {
        super("Client", Executors.newFixedThreadPool(2, new ThreadFactory() {

            private final AtomicInteger threadNum = new AtomicInteger();

            @Override
            public Thread newThread(@NotNull Runnable r) {
                var th = new Thread(r);
                th.setName("ClientSocketManagerImpl-thread-" + threadNum.getAndIncrement());
                return th;
            }
        }), socket);
    }
}
