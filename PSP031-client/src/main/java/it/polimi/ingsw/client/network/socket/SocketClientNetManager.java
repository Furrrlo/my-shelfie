package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import it.polimi.ingsw.socket.packets.LobbyPacket;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketClientNetManager implements ClientNetManager {
    private final ExecutorService threadPool;
    private final InetSocketAddress serverAddress;

    /** Maximum time to wait for a receive operation in {@link #defaultRecvTimeoutUnit}, or -1 to wait indefinitely */
    private final long defaultRecvTimeout;
    private final TimeUnit defaultRecvTimeoutUnit;

    private @Nullable ClientSocketManager socketManager;
    private @Nullable Socket socket;

    public SocketClientNetManager(InetSocketAddress serverAddress) {
        this(serverAddress, -1, TimeUnit.MILLISECONDS);
    }

    public SocketClientNetManager(InetSocketAddress serverAddress,
                                  long defaultRecvTimeout,
                                  TimeUnit defaultRecvTimeoutUnit) {
        this.serverAddress = serverAddress;
        this.defaultRecvTimeout = defaultRecvTimeout;
        this.defaultRecvTimeoutUnit = defaultRecvTimeoutUnit;
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
    public LobbyAndController<Lobby> joinGame(String nick) throws IOException {
        if (socketManager == null) {
            socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
            socketManager = defaultRecvTimeout == -1
                    ? new ClientSocketManagerImpl(socket)
                    : new ClientSocketManagerImpl(socket, defaultRecvTimeout, defaultRecvTimeoutUnit);
            socketManager.setNick(nick);
            System.out.println("Connected to : " + serverAddress);
        }

        try (var lobbyCtx = socketManager.send(new JoinGamePacket(nick), LobbyPacket.class)) {
            final var lobby = lobbyCtx.getPacket().lobby();
            CompletableFuture.runAsync(new SocketClientHeartbeatHandler(socketManager), threadPool)
                    .handle((__, ex) -> {
                        if (ex == null)
                            return __;
                        // TODO: reconnect
                        // TODO: logging
                        System.err.println("Uncaught exception in SocketClientHeartbeatHandler");
                        ex.printStackTrace();
                        return null;
                    });
            CompletableFuture.supplyAsync(new SocketLobbyClientUpdater(lobby, socketManager), threadPool)
                    .thenAccept(clientUpdater -> {
                        System.out.println("[Client][" + nick + "] shutting down lobby updater...");
                        if (clientUpdater != null)
                            clientUpdater.run();
                        System.out.println("[Client][" + nick + "] shutting down game updater...");
                    }).handle((__, ex) -> {
                        if (ex == null)
                            return __;
                        // TODO: reconnect
                        // TODO: logging
                        System.err.println("Uncaught exception in SocketClient*Updater");
                        ex.printStackTrace();
                        return null;
                    });
            return new LobbyAndController<>(lobby, new SocketLobbyController(socketManager));
        }
    }

    @VisibleForTesting
    @SuppressWarnings("NullAway")
    public void closeSocket() throws IOException {
        socket.close();
    }

    @VisibleForTesting
    public void kill() {
        //interrupt client updaters
        threadPool.shutdownNow();
    }
}
