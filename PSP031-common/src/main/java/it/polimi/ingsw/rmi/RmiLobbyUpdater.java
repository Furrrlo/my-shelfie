package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI remotable service which will be used to implement {@link LobbyUpdater}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link Adapter} is then used in order to be able to have an actual {@link LobbyUpdater}
 * interface implementation
 *
 * @see LobbyUpdater
 * @see Adapter
 */
public interface RmiLobbyUpdater extends Remote {

    /** RMI redeclaration of {@link LobbyUpdater#updateRequiredPlayers(int)}, check that for docs and details */
    void updateRequiredPlayers(int requiredPlayers) throws RemoteException;

    /** RMI redeclaration of {@link LobbyUpdater#updateJoinedPlayers(List)}, check that for docs and details */
    void updateJoinedPlayers(List<String> joinedPlayers) throws RemoteException;

    /** RMI redeclaration of {@link LobbyUpdater#updatePlayerReady(String, boolean)}, check that for docs and details */
    void updatePlayerReady(String nick, boolean ready) throws RemoteException;

    /** RMI redeclaration of {@link LobbyUpdater#updateGame(GameAndController)}, check that for docs and details */
    GameUpdater updateGame(GameAndController<Game> gameAndController) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyUpdater {

        private final RmiLobbyUpdater updater;

        public Adapter(RmiLobbyUpdater updater) {
            this.updater = updater;
        }

        @Override
        public void updateRequiredPlayers(int requiredPlayers) throws DisconnectedException {
            adapt(() -> updater.updateRequiredPlayers(requiredPlayers));
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
