package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketConnectionServerController implements Runnable {
    private final ExecutorService threadPool;
    private final ServerSocket socketServer;
    private final ServerController controller;

    public SocketConnectionServerController(ServerController controller, int port) throws IOException {
        this(controller, new ServerSocket(port));
    }

    @VisibleForTesting
    public SocketConnectionServerController(ServerController controller, ServerSocket serverSocket) throws IOException {
        this.controller = controller;
        this.threadPool = Executors.newFixedThreadPool(10, new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                var t = new Thread(r);
                t.setName("SocketConnectionServerController-socket-thread-" + threadNum.getAndIncrement());
                return t;
            }
        });
        this.socketServer = serverSocket;
    }

    @Override
    public void run() {
        do {
            try {
                final Socket socket = socketServer.accept();
                System.out.println("[Server] New client connected: " + socket.getRemoteSocketAddress());
                threadPool.submit(() -> {
                    try {
                        final ServerSocketManager socketManager = new ServerSocketManagerImpl(threadPool, socket);
                        var rec = socketManager.receive(JoinGamePacket.class);
                        JoinGamePacket p = rec.getPacket();
                        System.out.println("[Server] " + p.nick() + " is joining...");
                        socketManager.setNick(p.nick());
                        controller.joinGame(
                                p.nick(),
                                new SocketHeartbeatHandler(socketManager),
                                new SocketLobbyServerUpdaterFactory(socketManager, rec),
                                (serverPlayer, game) -> new SocketServerGameController(socketManager, serverPlayer, game));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!Thread.interrupted());
    }
}
