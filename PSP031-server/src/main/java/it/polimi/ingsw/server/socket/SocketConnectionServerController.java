package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.server.controller.BaseServerConnection;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.socket.SocketManager;
import it.polimi.ingsw.socket.packets.*;
import it.polimi.ingsw.utils.ThreadPools;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Socket-based service implementation which clients can use to establish a connection with the server.
 * <p>
 * This acts as the entry-point for networking for all clients that want to connect to the server using sockets.
 * Once a connection is established, this will delegate most of the job to the protocol-agnostic
 * {@link ServerController}, which will determine whether the player can connect.
 */
public class SocketConnectionServerController implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketConnectionServerController.class);

    private final ServerController controller;
    private final ExecutorService threadPool;
    private final ServerSocket socketServer;

    /**
     * Maximum time to wait without any input before considering a socket dead in {@link #readTimeoutUnit},
     * or -1 to wait indefinitely
     */
    private final long readTimeout;
    private final TimeUnit readTimeoutUnit;

    /** Maximum time to wait for a response in {@link #responseTimeoutUnit}, or -1 to wait indefinitely */
    private final long responseTimeout;
    private final TimeUnit responseTimeoutUnit;

    private final Future<?> acceptConnectionsTask;
    private final Set<PlayerConnection> connections = ConcurrentHashMap.newKeySet();

    public SocketConnectionServerController(ServerController controller, int port)
            throws IOException {
        this(controller, new ServerSocket(port), -1, TimeUnit.MILLISECONDS, -1, TimeUnit.MILLISECONDS);
    }

    public SocketConnectionServerController(ServerController controller,
                                            int port,
                                            long readTimeout,
                                            TimeUnit readTimeoutUnit,
                                            long responseTimeout,
                                            TimeUnit responseTimeoutUnit)
            throws IOException {
        this(controller, new ServerSocket(port), readTimeout, readTimeoutUnit, responseTimeout, responseTimeoutUnit);
    }

    @VisibleForTesting
    public SocketConnectionServerController(ServerController controller,
                                            ServerSocket serverSocket,
                                            long readTimeout,
                                            TimeUnit readTimeoutUnit,
                                            long responseTimeout,
                                            TimeUnit responseTimeoutUnit) {
        this.controller = controller;
        this.threadPool = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                .name("SocketConnectionServerController-thread-", 0)
                .factory());
        this.socketServer = serverSocket;
        this.readTimeout = readTimeout;
        this.readTimeoutUnit = readTimeoutUnit;
        this.responseTimeout = responseTimeout;
        this.responseTimeoutUnit = responseTimeoutUnit;
        this.acceptConnectionsTask = threadPool.submit(
                ThreadPools.giveNameToTask("SocketConnectionServerController-accept-thread", this::acceptConnectionsLoop));
    }

    @SuppressWarnings("FutureReturnValueIgnored") // We don't need to keep track of it as we shut down everything together
    private void acceptConnectionsLoop() {
        try {
            do {
                final Socket socket = socketServer.accept();
                socket.setTcpNoDelay(true);
                if (readTimeout != -1)
                    socket.setSoTimeout((int) readTimeoutUnit.toMillis(readTimeout));
                LOGGER.info("[Server] New socket client connected: {}", socket.getRemoteSocketAddress());
                threadPool.submit(ThreadPools.giveNameToTask(n -> n + "[doJoin]", () -> {
                    PlayerConnection connection;
                    try {
                        connection = doConnect(responseTimeout == -1
                                ? new ServerSocketManagerImpl(threadPool, socket)
                                : new ServerSocketManagerImpl(threadPool, socket, responseTimeout, responseTimeoutUnit));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to connect player", e);
                    }

                    if (connection != null)
                        connection.joinGameTask = threadPool.submit(ThreadPools.giveNameToTask(
                                n -> n + "[" + connection.getNick() + ":joinGameLoop]",
                                () -> joinGameLoop(connection)),
                                threadPool);
                }));
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
                LOGGER.error("Failed to close player socket", ex);
            }
        }

        acceptConnectionsTask.cancel(true);
        socketServer.close();
        threadPool.shutdown();
    }

    private @Nullable PlayerConnection doConnect(ServerSocketManager socketManager) throws IOException {
        final var joinCtx = socketManager.receive(JoinPacket.class);
        final var nick = joinCtx.getPacket().nick();
        LOGGER.info("[Server] {} is joining...", nick);
        socketManager.setNick(nick);

        final var connection = new PlayerConnection(controller, socketManager, nick);
        connections.add(connection);
        socketManager.setOnClose(connection::onSocketClose);
        try {
            var heartbeatHandler = new SocketHeartbeatHandler(socketManager, connection::disconnectPlayer);
            connection.heartbeatHandler = heartbeatHandler;
            controller.connectPlayer(nick, heartbeatHandler);
            connection.verifyNick();
            joinCtx.reply(new JoinedPacket());
            return connection;
        } catch (NickNotValidException e) {
            joinCtx.reply(new NickNotValidPacket(e.getMessage()), NickNotValidReceivedPacket.class).ack();
            connection.close();
        } catch (Throwable e) {
            connection.disconnectPlayer(e);
        }

        return null;
    }

    private void joinGameLoop(PlayerConnection connection) {
        try {
            do {
                doJoinGame(connection);
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedIOException ignored) {
            // Thread was interrupted to stop, normal control flow
        } catch (IOException | DisconnectedException ex) {
            connection.disconnectPlayer(ex);
        }
    }

    private void doJoinGame(PlayerConnection connection) throws IOException, DisconnectedException {
        var nick = connection.getNick();
        var socketManager = connection.getSocketManager();

        try (var joinCtx = connection.getSocketManager().receive(JoinGamePacket.class)) {
            var lobbyCtx = new AtomicReference<SocketManager.PacketReplyContext<C2SAckPacket, S2CAckPacket, LobbyReceivedPacket>>();
            controller.joinGame(
                    nick,
                    connection,
                    connection::onGameOver,
                    l -> {
                        try {
                            lobbyCtx.set(joinCtx.reply(new LobbyPacket(l.lobby()), LobbyReceivedPacket.class));
                            return new SocketLobbyServerUpdater(socketManager);
                        } catch (IOException e) {
                            throw new DisconnectedException(e);
                        }
                    },
                    lobbyController -> {
                        var socketController = new SocketServerLobbyController(socketManager, lobbyController, nick);
                        connection.lobbyControllerTask.set(threadPool.submit(ThreadPools.giveNameToTask(
                                n -> n + "[" + nick + ":lobbyController]",
                                () -> {
                                    try {
                                        socketController.run();
                                    } catch (Throwable t) {
                                        connection.disconnectPlayer(t);
                                    }
                                })));
                        return socketController;
                    },
                    (serverPlayer, game) -> {
                        var socketController = new SocketServerGameController(threadPool, socketManager, serverPlayer, game);
                        connection.gameControllerTask.set(threadPool.submit(ThreadPools.giveNameToTask(
                                n -> n + "[" + nick + ":gameController]",
                                () -> {
                                    try {
                                        socketController.run();
                                    } catch (Throwable t) {
                                        connection.disconnectPlayer(t);
                                    }
                                })));
                        return socketController;
                    });
            Objects.requireNonNull(lobbyCtx.get(), "Lobby was somehow not sent to the player").ack();
        }
    }

    private class PlayerConnection extends BaseServerConnection {

        private final ServerSocketManager socketManager;
        volatile @Nullable Closeable heartbeatHandler;
        volatile @Nullable Future<?> joinGameTask;
        final AtomicReference<@Nullable Future<?>> lobbyControllerTask = new AtomicReference<>();
        final AtomicReference<@Nullable Future<?>> gameControllerTask = new AtomicReference<>();

        private volatile @Nullable Closeable socketManagerDoClose;

        public PlayerConnection(ServerController controller,
                                ServerSocketManager socketManager,
                                String nick) {
            super(controller, nick);
            this.socketManager = socketManager;
        }

        public ServerSocketManager getSocketManager() {
            return socketManager;
        }

        @Override
        protected void verifyNick() {
            // Override to be able to access it in the outer class
            super.verifyNick();
        }

        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod") // It's called indirectly by doClose(...)
        public void close() throws IOException {
            // This call will bootstrap the socketManager close procedure, which will then
            // invoke doClose(...),, as it was previously registered using setOnClose
            // The actual close impl can be found in doClose(...)
            socketManager.close();
        }

        private void onSocketClose(Closeable socketManagerDoClose) throws IOException {
            this.socketManagerDoClose = socketManagerDoClose;
            try {
                super.close();
            } finally {
                this.socketManagerDoClose = null;
                connections.remove(this);
            }
        }

        @Override
        protected void doClose() throws IOException {
            var heartbeatHandler = this.heartbeatHandler;
            if (heartbeatHandler != null)
                heartbeatHandler.close();
            var joinGameTask = this.joinGameTask;
            if (joinGameTask != null)
                joinGameTask.cancel(true);
            var lobbyControllerTask = this.lobbyControllerTask.getAndSet(null);
            if (lobbyControllerTask != null)
                lobbyControllerTask.cancel(true);
            var gameControllerTask = this.gameControllerTask.getAndSet(null);
            if (gameControllerTask != null)
                gameControllerTask.cancel(true);
            var socketManagerDoClose = this.socketManagerDoClose;
            if (socketManagerDoClose != null)
                socketManagerDoClose.close();
        }

        @Override
        protected void callDisconnectPlayerHook() {
            // If we are in an interrupted thread (might be the socket recv/read thread which triggered the close)
            // we need to clear the state in order to be able to call potentially interrupting methods
            // We do it in a complete different thread as subsequent close calls might re-interrupt the thread,
            // so instead we use Future#getUninterruptibly() which should handle this properly
            try {
                ThreadPools.getUninterruptibly(threadPool.submit(super::callDisconnectPlayerHook));
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to invoke disconnectPlayerHook uninterruptibly", e.getCause());
            }
        }

        @Override
        protected void doClosePlayerGame() {
            var lobbyControllerTask = this.lobbyControllerTask.getAndSet(null);
            if (lobbyControllerTask != null)
                lobbyControllerTask.cancel(true);
            var gameControllerTask = this.gameControllerTask.getAndSet(null);
            if (gameControllerTask != null)
                gameControllerTask.cancel(true);
        }
    }
}
