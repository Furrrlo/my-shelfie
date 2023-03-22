package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.updater.LobbyUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import it.polimi.ingsw.model.Lobby;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiLobbyUpdaterFactory extends Remote {

    RmiLobbyUpdater create(Lobby lobby) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyUpdaterFactory {

        private final RmiLobbyUpdaterFactory updater;

        public Adapter(RmiLobbyUpdaterFactory updater) {
            this.updater = updater;
        }

        @Override
        public LobbyUpdater create(Lobby lobby) throws DisconnectedException {
            return adapt(() -> new RmiLobbyUpdater.Adapter(updater.create(lobby)));
        }
    }
}
