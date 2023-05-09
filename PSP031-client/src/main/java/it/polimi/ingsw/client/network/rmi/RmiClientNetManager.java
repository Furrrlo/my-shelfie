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
import java.util.concurrent.TimeUnit;

public class RmiClientNetManager extends RmiAdapter implements ClientNetManager {

    private final @Nullable String host;
    private final int port;
    private final String remoteName;
    private final UnicastRemoteObjects.Exporter unicastRemoteObjects;
    private @Nullable RmiConnectionController server;

    public RmiClientNetManager() {
        this(null, Registry.REGISTRY_PORT);
    }

    public RmiClientNetManager(@Nullable String host, int port) {
        this(host, port, RmiConnectionController.REMOTE_NAME);
    }

    @VisibleForTesting
    public RmiClientNetManager(@Nullable String host, int port, String remoteName) {
        this(host, port, remoteName,
                new RMITimeoutClientSocketFactory(500, TimeUnit.MILLISECONDS),
                null);
    }

    @VisibleForTesting
    public RmiClientNetManager(@Nullable String host,
                               int port,
                               String remoteName,
                               @Nullable RMIClientSocketFactory csf,
                               @Nullable RMIServerSocketFactory ssf) {
        this.port = port;
        this.remoteName = remoteName;
        this.host = host;
        this.unicastRemoteObjects = UnicastRemoteObjects.createExporter(csf, ssf);
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
    public LobbyAndController<Lobby> joinGame(String nick) throws RemoteException, NotBoundException, NickNotValidException {
        if (server == null) {
            final Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RmiConnectionController) registry.lookup(remoteName);
        }

        final InterceptingFactory updaterFactory = new InterceptingFactory(unicastRemoteObjects);
        var heartbeatClientHandler = new RmiHeartbeatClientHandler(
                () -> disconnectPlayer(updaterFactory.getUpdater().getLobbyAndController().lobby(), nick));
        server.joinGame(
                nick,
                unicastRemoteObjects.export(heartbeatClientHandler, 0),
                unicastRemoteObjects.export(updaterFactory, 0));

        heartbeatClientHandler.start();
        return updaterFactory.getUpdater().getLobbyAndController();
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
