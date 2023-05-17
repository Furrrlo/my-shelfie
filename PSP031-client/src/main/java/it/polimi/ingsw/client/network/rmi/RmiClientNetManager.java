package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class RmiClientNetManager extends RmiAdapter implements ClientNetManager {

    private final @Nullable String host;
    private final int port;
    private final String remoteName;
    private final String nick;
    private final RmiConnectionController.ConnectedController server;
    private final UnicastRemoteObjects.Exporter unicastRemoteObjects;

    private volatile @Nullable Lobby lobby;

    public static ClientNetManager connect(@Nullable String host, int port, String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, RmiConnectionController.REMOTE_NAME, nick);
    }

    @VisibleForTesting
    public static RmiClientNetManager connect(@Nullable String host, int port, String remoteName, String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, remoteName, new RMITimeoutClientSocketFactory(), null, nick);
    }

    @VisibleForTesting
    public static RmiClientNetManager connect(@Nullable String host,
                                              int port,
                                              String remoteName,
                                              @Nullable RMIClientSocketFactory csf,
                                              @Nullable RMIServerSocketFactory ssf,
                                              String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        return connect(host, port, remoteName, UnicastRemoteObjects.createExporter(csf, ssf), nick);
    }

    private static RmiClientNetManager connect(@Nullable String host,
                                               int port,
                                               String remoteName,
                                               UnicastRemoteObjects.Exporter unicastRemoteObjects,
                                               String nick)
            throws RemoteException, NotBoundException, NickNotValidException {
        final Registry registry = LocateRegistry.getRegistry(host, port);
        final var server = (RmiConnectionController) registry.lookup(remoteName);

        AtomicReference<RmiClientNetManager> netManagerRef = new AtomicReference<>();
        var heartbeatClientHandler = new RmiHeartbeatClientHandler(() -> {
            Lobby lobby;
            var netManager = netManagerRef.get();
            if (netManager != null && (lobby = netManager.lobby) != null)
                lobby.disconnectThePlayer(nick);
        });
        var connectedServer = server.doConnect(nick, unicastRemoteObjects.export(heartbeatClientHandler, 0));
        heartbeatClientHandler.start();

        var netManager = new RmiClientNetManager(host, port, remoteName, nick, connectedServer, unicastRemoteObjects);
        netManagerRef.set(netManager);
        return netManager;
    }

    @VisibleForTesting
    private RmiClientNetManager(@Nullable String host,
                                int port,
                                String remoteName,
                                String nick,
                                RmiConnectionController.ConnectedController server,
                                UnicastRemoteObjects.Exporter unicastRemoteObjects) {
        this.port = port;
        this.server = server;
        this.remoteName = remoteName;
        this.nick = nick;
        this.host = host;
        this.unicastRemoteObjects = unicastRemoteObjects;
    }

    @Override
    public ClientNetManager recreateAndReconnect() throws NotBoundException, RemoteException, NickNotValidException {
        return RmiClientNetManager.connect(host, port, remoteName, unicastRemoteObjects, nick);
    }

    @Override
    public String getHost() {
        return host == null ? "localhost" : host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public LobbyAndController<Lobby> joinGame() throws RemoteException {
        final InterceptingFactory updaterFactory = new InterceptingFactory(unicastRemoteObjects);
        server.joinGame(unicastRemoteObjects.export(updaterFactory, 0));

        var lobbyAndController = updaterFactory.getUpdater().getLobbyAndController();
        lobby = lobbyAndController.lobby();
        return lobbyAndController;
    }

    private static class InterceptingFactory implements RmiLobbyUpdaterFactory {

        private final UnicastRemoteObjects.Exporter unicastRemoteObjects;
        private @Nullable RmiLobbyClientUpdater updater;

        public InterceptingFactory(UnicastRemoteObjects.Exporter unicastRemoteObjects) {
            this.unicastRemoteObjects = unicastRemoteObjects;
        }

        public RmiLobbyClientUpdater getUpdater() {
            return Objects.requireNonNull(
                    updater,
                    "Expected ConnectionServerController to invoke " +
                            "GameCreationStateUpdaterFactory exactly once, was 0");
        }

        @Override
        public RmiLobbyUpdater create(LobbyAndController<Lobby> lobby) throws RemoteException {
            if (updater != null)
                throw new UnsupportedOperationException("Expected ConnectionServerController to invoke " +
                        "GameCreationStateUpdaterFactory exactly once, was 2+");

            updater = new RmiLobbyClientUpdater(unicastRemoteObjects, lobby);
            return unicastRemoteObjects.export(updater, 0);
        }
    }
}
