package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.util.Objects;

public class RmiClientNetManager extends RmiAdapter implements ClientNetManager {

    private final @Nullable String host;
    private final int port;
    private final String remoteName;
    private @Nullable RmiConnectionController server;

    public RmiClientNetManager() {
        this(null, Registry.REGISTRY_PORT);
    }

    public RmiClientNetManager(@Nullable String host, int port) {
        this(host, port, RmiConnectionController.REMOTE_NAME);
    }

    @VisibleForTesting
    public RmiClientNetManager(@Nullable String host, int port, String remoteName) {
        this(host, port, remoteName, new RMITimeoutSocketFactory());
    }

    @VisibleForTesting
    public RmiClientNetManager(@Nullable String host, int port, String remoteName, RMISocketFactory socketFactory) {
        this.port = port;
        this.remoteName = remoteName;
        this.host = host;
        try {
            RMISocketFactory.setSocketFactory(socketFactory);
        } catch (IOException e) {
            //this will happen in tests, ignored.
        }
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
    public LobbyAndController<Lobby> joinGame(String nick) throws RemoteException, NotBoundException {
        if (server == null) {
            final Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RmiConnectionController) registry.lookup(remoteName);
        }

        final InterceptingFactory updaterFactory = new InterceptingFactory();
        server.joinGame(
                nick,
                UnicastRemoteObjects.export(new RmiHeartbeatClientHandler(), 0),
                UnicastRemoteObjects.export(updaterFactory, 0));
        return updaterFactory.getUpdater().getLobbyAndController();
    }

    private static class InterceptingFactory implements RmiLobbyUpdaterFactory {
        private @Nullable RmiLobbyClientUpdater updater;

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

            updater = new RmiLobbyClientUpdater(lobby);
            return UnicastRemoteObjects.export(updater, 0);
        }
    }
}
