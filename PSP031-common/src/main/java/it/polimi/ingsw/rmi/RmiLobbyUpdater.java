package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RmiLobbyUpdater extends Remote {

    void updateJoinedPlayers(List<String> joinedPlayers) throws RemoteException;

    void updatePlayerReady(String nick, boolean ready) throws RemoteException;

    GameUpdater updateGame(GameAndController<Game> gameAndController) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyUpdater {

        private final RmiLobbyUpdater updater;

        public Adapter(RmiLobbyUpdater updater) {
            this.updater = updater;
        }

        @Override
        public void updateJoinedPlayers(List<String> joinedPlayers) throws DisconnectedException {
            adapt(() -> updater.updateJoinedPlayers(joinedPlayers));
        }

        @Override
        public void updatePlayerReady(String nick, boolean ready) throws DisconnectedException {
            adapt(() -> updater.updatePlayerReady(nick, ready));
        }

        @Override
        public GameUpdater updateGame(GameAndController<Game> gameAndController) throws DisconnectedException {
            return adapt(() -> updater.updateGame(gameAndController));
        }
    }
}
