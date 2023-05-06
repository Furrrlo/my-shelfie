package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.socket.packets.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketClientNetManager implements ClientNetManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClientNetManager.class);

    private final ExecutorService threadPool;
    private final InetSocketAddress serverAddress;

    /** Maximum time to wait for a receive operation in {@link #defaultResponseTimeoutUnit}, or -1 to wait indefinitely */
    private final long defaultResponseTimeout;
    private final TimeUnit defaultResponseTimeoutUnit;

    private @Nullable ClientSocketManager socketManager;
    private @Nullable Socket socket;

    /**
     * Socket can't be reconnected after a disconnection or a failed connection.
     * If the connection fails, {@link Socket#isClosed()} does not return true,
     * but the {@link SocketImpl} is closed by {@link sun.nio.ch.NioSocketImpl#connect(SocketAddress, int)}
     * and can't be reconnected.
     */
    @SuppressWarnings("JavadocReference")
    private boolean newSocketNeeded = false;

    public SocketClientNetManager(InetSocketAddress serverAddress) {
        this(serverAddress, -1, TimeUnit.MILLISECONDS);
    }

    public SocketClientNetManager(InetSocketAddress serverAddress,
                                  long defaultResponseTimeout,
                                  TimeUnit defaultResponseTimeoutUnit) {
        this(serverAddress, defaultResponseTimeout, defaultResponseTimeoutUnit, null);
    }

    @VisibleForTesting
    public SocketClientNetManager(InetSocketAddress serverAddress,
                                  long defaultResponseTimeout,
                                  TimeUnit defaultResponseTimeoutUnit,
                                  @Nullable Socket socket) {
        this.serverAddress = serverAddress;
        this.defaultResponseTimeout = defaultResponseTimeout;
        this.defaultResponseTimeoutUnit = defaultResponseTimeoutUnit;
        this.socket = socket;
        this.threadPool = Executors.newFixedThreadPool(2, r -> {
            var th = new Thread(r);
            th.setName("ClientUpdater-thread");
            return th;
        });
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
    public LobbyAndController<Lobby> joinGame(String nick) throws IOException, NickNotValidException {
        if (socketManager == null || socketManager.isClosed()) {
            //Log socket status
            if (socket != null) {
                LOGGER.trace("Socket closed: " + socket.isClosed());
                LOGGER.trace("Socket connected: " + socket.isConnected());
                LOGGER.trace("Socket bounded: " + socket.isBound());
                LOGGER.trace("newSocketNeeded: " + newSocketNeeded);
            } else {
                LOGGER.trace("Socket null");
            }

            //Create a new socket if needed
            if (socket == null || socket.isClosed() || newSocketNeeded) {
                socket = new Socket();
                newSocketNeeded = false;
            }

            if (!socket.isConnected()) {
                newSocketNeeded = true;
                socket.connect(serverAddress, 500);
                socket.setSoTimeout(10000);
                newSocketNeeded = false;
            }
            socketManager = defaultResponseTimeout == -1
                    ? new ClientSocketManagerImpl(socket)
                    : new ClientSocketManagerImpl(socket, defaultResponseTimeout, defaultResponseTimeoutUnit);
            socketManager.setNick(nick);
            LOGGER.info("Connected to : " + serverAddress);
        }

        try (var lobbyCtx = socketManager.send(new JoinGamePacket(nick), JoinResponsePacket.class)) {
            switch (lobbyCtx.getPacket()) {
                case NickNotValidPacket p -> {
                    lobbyCtx.reply(new LobbyReceivedPacket());
                    socketManager.close();
                    throw new NickNotValidException(p.message());
                }
                case LobbyPacket p -> {
                    final var lobby = p.lobby();
                    CompletableFuture.runAsync(new SocketClientHeartbeatHandler(socketManager), threadPool)
                            .handle((__, ex) -> {
                                if (ex == null)
                                    return __;
                                LOGGER.error("Uncaught exception in SocketClientHeartbeatHandler", ex);
                                disconnectPlayer(lobby, nick);
                                return null;
                            });
                    CompletableFuture.supplyAsync(new SocketLobbyClientUpdater(lobby, socketManager), threadPool)
                            .thenAccept(clientUpdater -> {
                                LOGGER.info("[Client] [" + nick + "] shut down lobby updater");

                                if (clientUpdater != null) {
                                    clientUpdater.run();
                                    LOGGER.info("[Client] [" + nick + "] shut down game updater");
                                }
                            }).handle((__, ex) -> {
                                if (ex == null)
                                    return __;
                                LOGGER.error("Uncaught exception in SocketClient*Updater", ex);
                                disconnectPlayer(lobby, nick);
                                return null;
                            });
                    lobbyCtx.reply(new LobbyReceivedPacket());
                    return new LobbyAndController<>(lobby, new SocketLobbyController(socketManager));
                }
            }
        }
        throw new RuntimeException("Why is this necessary?");
    }

    private void disconnectPlayer(Lobby lobby, String nick) {
        LOGGER.warn("Socket disconnected from the server");
        var game = lobby.game().get();
        if (game == null) {
            lobby.joinedPlayers().update(players -> {
                List<LobbyPlayer> l = new ArrayList<>(players);
                l.removeIf(p -> p.getNick().equals(nick));
                return l;
            });
        } else {
            game.game().thePlayer().connected().set(false);
        }
    }
}
