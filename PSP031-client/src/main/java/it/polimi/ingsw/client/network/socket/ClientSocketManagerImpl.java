package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.socket.SocketManagerImpl;
import it.polimi.ingsw.socket.packets.C2SAckPacket;
import it.polimi.ingsw.socket.packets.C2SPacket;
import it.polimi.ingsw.socket.packets.S2CAckPacket;
import it.polimi.ingsw.socket.packets.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSocketManagerImpl
        extends SocketManagerImpl<S2CPacket, S2CAckPacket, C2SAckPacket, C2SPacket>
        implements ClientSocketManager {

    private final ExecutorService executor;

    public ClientSocketManagerImpl(Socket socket) throws IOException {
        this(createDefaultExecutor(), socket);
    }

    public ClientSocketManagerImpl(Socket socket,
                                   long defaultResponseTimeout,
                                   TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        this(createDefaultExecutor(), socket, defaultResponseTimeout, defaultResponseTimeoutUnit);
    }

    private ClientSocketManagerImpl(ExecutorService executor, Socket socket) throws IOException {
        super("Client", executor, socket);
        this.executor = executor;
    }

    private ClientSocketManagerImpl(ExecutorService executor,
                                    Socket socket,
                                    long defaultResponseTimeout,
                                    TimeUnit defaultResponseTimeoutUnit)
            throws IOException {
        super("Client", executor, socket, defaultResponseTimeout, defaultResponseTimeoutUnit);
        this.executor = executor;
    }

    private static ExecutorService createDefaultExecutor() {
        return Executors.newFixedThreadPool(2, new ThreadFactory() {

            private final AtomicInteger threadNum = new AtomicInteger();

            @Override
            public Thread newThread(@NotNull Runnable r) {
                var th = new Thread(r);
                th.setName("ClientSocketManagerImpl-thread-" + threadNum.getAndIncrement());
                return th;
            }
        });
    }

    @Override
    public void close() throws IOException {
        super.close();

        if (!executor.isShutdown())
            executor.shutdown();
    }
}
