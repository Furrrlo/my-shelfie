package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyView;
import it.polimi.ingsw.rmi.RmiConnectionController;
import it.polimi.ingsw.rmi.RmiLobbyUpdater;
import it.polimi.ingsw.rmi.RmiLobbyUpdaterFactory;
import it.polimi.ingsw.rmi.UnicastRemoteObjects;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class RmiClientNetManager implements ClientNetManager {

    private final String remoteName;

    public RmiClientNetManager() {
        this(RmiConnectionController.REMOTE_NAME);
    }

    @VisibleForTesting
    public RmiClientNetManager(String remoteName) {
        this.remoteName = remoteName;
    }

    @Override
    public LobbyView joinGame(String nick) throws RemoteException, NotBoundException {
        final Registry registry = LocateRegistry.getRegistry();
        final RmiConnectionController server = (RmiConnectionController) registry.lookup(remoteName);

        final InterceptingFactory updaterFactory = new InterceptingFactory();
        server.joinGame(
                nick,
                UnicastRemoteObjects.export(updaterFactory, 0));
        return updaterFactory.getUpdater().getGameCreationState();
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
        public RmiLobbyUpdater create(Lobby lobby) throws RemoteException {
            if(updater != null)
                throw new UnsupportedOperationException("Expected ConnectionServerController to invoke " +
                        "GameCreationStateUpdaterFactory exactly once, was 2+");

            updater = new RmiLobbyClientUpdater(lobby);
            return UnicastRemoteObjects.export(updater, 0);
        }
    }
}
