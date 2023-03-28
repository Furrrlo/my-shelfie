package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.LobbyView;
import it.polimi.ingsw.socket.packets.JoinGamePacket;
import it.polimi.ingsw.socket.packets.LobbyPacket;
import it.polimi.ingsw.socket.packets.ReadyPacket;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClientNetManager implements ClientNetManager {
    private final ExecutorService threadPool;
    private final InetSocketAddress serverAddress;
    private @Nullable ClientSocketManager socketManager;

    public SocketClientNetManager(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        this.threadPool = Executors.newFixedThreadPool(2, r -> {
            var th = new Thread(r);
            th.setName("ClientUpdater-thread");
            return th;
        });
    }

    @Override
    public LobbyView joinGame(String nick) throws IOException {
        if (socketManager == null) {
            socketManager = new ClientSocketManagerImpl(new Socket(serverAddress.getAddress(), serverAddress.getPort()));
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
                    .thenAcceptAsync(clientUpdater -> {
                        if (clientUpdater != null)
                            clientUpdater.run();
                    }).handle((__, ex) -> {
                        if (ex == null)
                            return __;
                        // TODO: reconnect
                        // TODO: logging
                        System.err.println("Uncaught exception in SocketClient*Updater");
                        ex.printStackTrace();
                        return null;
                    });
            return lobby;
        }
    }

    @Override
    public void ready(boolean ready) throws DisconnectedException {
        if (socketManager == null)
            throw new UnsupportedOperationException("You have not joined a game");

        try {
            socketManager.send(new ReadyPacket(ready));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
