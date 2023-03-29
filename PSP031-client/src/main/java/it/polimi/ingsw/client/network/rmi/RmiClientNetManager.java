package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.rmi.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class RmiClientNetManager extends RmiAdapter implements ClientNetManager {

    private final String remoteName;
    private @Nullable RmiConnectionController server;

    public RmiClientNetManager() {
        this(RmiConnectionController.REMOTE_NAME);
    }

    @VisibleForTesting
    public RmiClientNetManager(String remoteName) {
        this.remoteName = remoteName;
    }

    @Override
    public LobbyAndController<Lobby> joinGame(String nick) throws RemoteException, NotBoundException {
        if (server == null) {
            final Registry registry = LocateRegistry.getRegistry();
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
