package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NetworkConstants;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.packets.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketClientNetManager implements ClientNetManager {

    public static final int DEFAULT_PORT = 1234;
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClientNetManager.class);

    private final ExecutorService threadPool;
    private final InetSocketAddress serverAddress;
    private final ClientSocketManager socketManager;
    private final String nick;

    private final long readTimeout;
    private final TimeUnit readTimeoutUnit;

    private volatile @Nullable Future<?> heartbeatHandlerTask;
    private volatile @Nullable Future<?> updaterTask;
    private volatile @Nullable Lobby lobby;

    public static ClientNetManager connect(InetSocketAddress serverAddress, String nick)
            throws IOException, NickNotValidException {
        return connect(
                serverAddress,
                TimeUnit.MILLISECONDS.convert(NetworkConstants.READ_TIMEOUT), TimeUnit.MILLISECONDS,
                TimeUnit.MILLISECONDS.convert(NetworkConstants.RESPONSE_TIMEOUT), TimeUnit.MILLISECONDS,
                nick);
    }

    public static ClientNetManager connect(InetSocketAddress serverAddress,
                                           long readTimeout,
                                           TimeUnit readTimeoutUnit,
                                           long responseTimeout,
                                           TimeUnit responseTimeoutUnit,
                                           String nick)
            throws IOException, NickNotValidException {
        return connect(serverAddress, readTimeout, readTimeoutUnit, responseTimeout, responseTimeoutUnit, new Socket(), nick);
    }

    @VisibleForTesting
    public static SocketClientNetManager connect(InetSocketAddress serverAddress,
                                                 long readTimeout,
                                                 TimeUnit readTimeoutUnit,
                                                 long responseTimeout,
                                                 TimeUnit responseTimeoutUnit,
                                                 Socket socket,
                                                 String nick)
            throws IOException, NickNotValidException {
        if (readTimeout == -1) {
            socket.connect(serverAddress);
        } else {
            var readTimeoutMillis = (int) readTimeoutUnit.toMillis(readTimeout);
            socket.connect(serverAddress, readTimeoutMillis);
            socket.setSoTimeout(readTimeoutMillis);
        }
        socket.setTcpNoDelay(true);

        ClientSocketManager socketManager;
        try {
            socketManager = responseTimeout == -1
                    ? new ClientSocketManagerImpl(socket)
                    : new ClientSocketManagerImpl(socket, responseTimeout, responseTimeoutUnit);
            socketManager.setNick(nick);
        } catch (Throwable t) {
            socket.close();
            throw t;
        }

        LOGGER.info("Connected to : " + serverAddress);

        try {
            var netManager = new SocketClientNetManager(serverAddress, socketManager, readTimeout, readTimeoutUnit, nick);
            netManager.doConnect();
            return netManager;
        } catch (Throwable t) {
            socketManager.close();
            throw t;
        }
    }

    private SocketClientNetManager(InetSocketAddress serverAddress,
                                   ClientSocketManager socketManager,
                                   long readTimeout,
                                   TimeUnit readTimeoutUnit,
                                   String nick) {
        this.serverAddress = serverAddress;
        this.socketManager = socketManager;
        this.readTimeout = readTimeout;
        this.readTimeoutUnit = readTimeoutUnit;
        this.nick = nick;
        this.threadPool = Executors.newFixedThreadPool(2, r -> {
            var th = new Thread(r);
            th.setName("ClientUpdater-thread");
            return th;
        });
    }

    @Override
    public ClientNetManager recreateAndReconnect() throws Exception {
        return SocketClientNetManager.connect(serverAddress,
                readTimeout,
                readTimeoutUnit,
                socketManager.getDefaultResponseTimeout(),
                socketManager.getDefaultResponseTimeoutUnit(),
                new Socket(),
                nick);
    }

    private void doConnect() throws IOException, NickNotValidException {
        try (var lobbyCtx = socketManager.send(new JoinPacket(nick), JoinResponsePacket.class)) {
            switch (lobbyCtx.getPacket()) {
                case NickNotValidPacket p -> {
                    var nickException = new NickNotValidException(p.message());

                    try {
                        lobbyCtx.reply(new LobbyReceivedPacket());
                    } catch (IOException ex) {
                        // We ignore exceptions on the last ack receival, because the socket may
                        // be closed before we are able to read out the last ack packet
                        // We don't care about whether the server has received this anyway,
                        // we can just hope it did and go on
                        ex.addSuppressed(new IOException("Failed to send last LobbyReceivedPacket ack", ex));
                    }

                    try {
                        close();
                    } catch (IOException e) {
                        nickException.addSuppressed(new IOException("Failed to close socket", e));
                    }

                    throw nickException;
                }
                case JoinedPacket ignored -> heartbeatHandlerTask = CompletableFuture
                        .runAsync(new SocketClientHeartbeatHandler(socketManager), threadPool)
                        .handle((__, ex) -> {
                            if (ex == null)
                                return __;

                            try {
                                close();
                            } catch (IOException e) {
                                ex.addSuppressed(new IOException("Failed to close SocketClientNetManager", e));
                            }

                            LOGGER.error("Uncaught exception in SocketClientHeartbeatHandler", ex);
                            return null;
                        });
            }
        }
    }

    @Override
    public String getHost() {
        return serverAddress.getHostName();
    }

    @Override
    public int getPort() {
        return serverAddress.getPort();
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public LobbyAndController<Lobby> joinGame() throws IOException {
        try (var lobbyCtx = socketManager.send(new JoinGamePacket(), LobbyPacket.class)) {
            final var lobby = lobbyCtx.getPacket().lobby();
            this.lobby = lobby;

            var updaterTask = this.updaterTask;
            if (updaterTask != null)
                updaterTask.cancel(true);
            this.updaterTask = CompletableFuture
                    .supplyAsync(new SocketLobbyClientUpdater(lobby, socketManager), threadPool)
                    .thenAccept(clientUpdater -> {
                        LOGGER.info("[Client] [" + nick + "] shut down lobby updater");

                        if (clientUpdater != null) {
                            clientUpdater.run();
                            LOGGER.info("[Client] [" + nick + "] shut down game updater");
                        }
                    }).handle((__, ex) -> {
                        if (ex == null)
                            return __;

                        try {
                            close();
                        } catch (IOException e) {
                            ex.addSuppressed(new IOException("Failed to close SocketClientNetManager", e));
                        }

                        LOGGER.error("Uncaught exception in SocketClient*Updater", ex);
                        return null;
                    });
            lobbyCtx.reply(new LobbyReceivedPacket());
            return new LobbyAndController<>(lobby, new SocketLobbyController(socketManager));
        }
    }

    @Override
    public void close() throws IOException {
        var heartbeatHandlerTask = this.heartbeatHandlerTask;
        if (heartbeatHandlerTask != null)
            heartbeatHandlerTask.cancel(true);
        var updaterTask = this.updaterTask;
        if (updaterTask != null)
            updaterTask.cancel(true);
        socketManager.close();

        var lobby = this.lobby;
        if (lobby != null)
            lobby.disconnectThePlayer(nick);
    }
}
