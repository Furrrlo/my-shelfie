package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
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
        try {
            do {
                final Socket socket = socketServer.accept();
                System.out.println("[Server] New client connected: " + socket.getRemoteSocketAddress());
                threadPool.submit(() -> {
                    try {
                        doJoin(new ServerSocketManagerImpl(threadPool, socket));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } while (!Thread.interrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void doJoin(ServerSocketManager socketManager) throws IOException {
        final var rec = socketManager.receive(JoinGamePacket.class);
        final var nick = rec.getPacket().nick();
        System.out.println("[Server] " + nick + " is joining...");
        socketManager.setNick(nick);

        controller.joinGame(
                nick,
                new SocketHeartbeatHandler(socketManager),
                new SocketLobbyServerUpdaterFactory(socketManager, rec),
                lobbyController -> {
                    var socketController = new SocketServerLobbyController(socketManager, lobbyController, nick);
                    CompletableFuture.runAsync(socketController, threadPool).handle((__, ex) -> {
                        if (ex == null)
                            return __;

                        controller.disconnectPlayer(nick, ex);
                        return __;
                    });
                    return socketController;
                },
                (serverPlayer, game) -> {
                    var socketController = new SocketServerGameController(socketManager, serverPlayer, game);
                    CompletableFuture.runAsync(socketController, threadPool).handle((__, ex) -> {
                        if (ex == null)
                            return __;

                        controller.disconnectPlayer(nick, ex);
                        return __;
                    });
                    return socketController;
                });
    }
}
