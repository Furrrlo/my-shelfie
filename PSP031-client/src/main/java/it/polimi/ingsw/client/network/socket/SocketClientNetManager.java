package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.packets.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
            if (socket == null || socket.isClosed())
                socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
            else
                socket.connect(serverAddress);
            socket.setSoTimeout(22000);
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
                                // TODO: reconnect
                                LOGGER.error("Uncaught exception in SocketClientHeartbeatHandler", ex);
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
                                // TODO: reconnect
                                LOGGER.error("Uncaught exception in SocketClient*Updater", ex);
                                return null;
                            });
                    lobbyCtx.reply(new LobbyReceivedPacket());
                    return new LobbyAndController<>(lobby, new SocketLobbyController(socketManager));
                }
            }
        }
        throw new RuntimeException("Why is this necessary?");
    }
}
