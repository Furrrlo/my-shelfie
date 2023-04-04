package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.server.controller.BaseServerConnection;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketConnectionServerController implements Closeable {

    private final ServerController controller;
    private final ExecutorService threadPool;
    private final ServerSocket socketServer;
    private final Future<?> acceptConnectionsTask;
    private final Set<PlayerConnection> connections = ConcurrentHashMap.newKeySet();

    public SocketConnectionServerController(ServerController controller, int port) throws IOException {
        this(controller, new ServerSocket(port));
    }

    @VisibleForTesting
    public SocketConnectionServerController(ServerController controller, ServerSocket serverSocket) throws IOException {
        this.controller = controller;
        this.threadPool = Executors.newFixedThreadPool(20, new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                var t = new Thread(r);
                t.setName("SocketConnectionServerController-socket-thread-" + threadNum.getAndIncrement());
                return t;
            }
        });
        this.socketServer = serverSocket;
        this.acceptConnectionsTask = threadPool.submit(this::acceptConnectionsLoop);
    }

    private void acceptConnectionsLoop() {
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

    @Override
    public void close() throws IOException {
        for (PlayerConnection c : connections) {
            try {
                c.close();
            } catch (IOException ex) {
                // TODO: log
                System.err.println("Failed to close player socket");
                ex.printStackTrace();
            }
        }

        acceptConnectionsTask.cancel(true);
        socketServer.close();
        threadPool.shutdown();
    }

    private void doJoin(ServerSocketManager socketManager) throws IOException {
        final var rec = socketManager.receive(JoinGamePacket.class);
        final var nick = rec.getPacket().nick();
        System.out.println("[Server] " + nick + " is joining...");
        socketManager.setNick(nick);

        final var connection = new PlayerConnection(controller, socketManager, nick);
        final var heartbeatHandler = new SocketHeartbeatHandler(socketManager);
        connections.add(connection);
        controller.joinGame(
                nick,
                clock -> {
                    try {
                        heartbeatHandler.sendHeartbeat(Instant.now(clock));
                    } catch (DisconnectedException e) {
                        connection.disconnectPlayer(e);
                    }
                },
                connection,
                new SocketLobbyServerUpdaterFactory(socketManager, rec),
                lobbyController -> {
                    var socketController = new SocketServerLobbyController(socketManager, lobbyController, nick);
                    connection.lobbyControllerTask = CompletableFuture.runAsync(socketController, threadPool)
                            .handle((__, ex) -> {
                                if (ex == null)
                                    return __;

                                connection.disconnectPlayer(ex);
                                return __;
                            });
                    return socketController;
                },
                (serverPlayer, game) -> {
                    var socketController = new SocketServerGameController(socketManager, serverPlayer, game);
                    connection.gameControllerTask = CompletableFuture.runAsync(socketController, threadPool)
                            .handle((__, ex) -> {
                                if (ex == null)
                                    return __;

                                connection.disconnectPlayer(ex);
                                return __;
                            });
                    return socketController;
                });
    }

    private class PlayerConnection extends BaseServerConnection {

        private final ServerSocketManager socketManager;
        volatile @Nullable Future<?> lobbyControllerTask;
        volatile @Nullable Future<?> gameControllerTask;

        public PlayerConnection(ServerController controller,
                                ServerSocketManager socketManager,
                                String nick) {
            super(controller, nick);
            this.socketManager = socketManager;
        }

        @Override
        public void close() throws IOException {
            try {
                var lobbyControllerTask = this.lobbyControllerTask;
                if (lobbyControllerTask != null)
                    lobbyControllerTask.cancel(true);
                var gameControllerTask = this.gameControllerTask;
                if (gameControllerTask != null)
                    gameControllerTask.cancel(true);
                socketManager.close();
            } finally {
                connections.remove(this);
            }
        }
    }
}
