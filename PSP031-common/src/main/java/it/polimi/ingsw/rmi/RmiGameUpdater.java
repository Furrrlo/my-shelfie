package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.model.UserMessage;
import it.polimi.ingsw.updater.GameUpdater;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI remotable service which will be used to implement {@link GameUpdater}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link Adapter} is then used in order to be able to have an actual {@link GameUpdater}
 * interface implementation
 *
 * @see GameUpdater
 * @see Adapter
 */
public interface RmiGameUpdater extends Remote {

    /** RMI redeclaration of {@link GameUpdater#updateBoardTile(int, int, Tile)}, check that for docs and details */
    void updateBoardTile(int row, int col, @Nullable Tile tile) throws RemoteException;

    /**
     * RMI redeclaration of {@link GameUpdater#updatePlayerShelfieTile(String, int, int, Tile)}, check that for docs and details
     */
    void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updatePlayerConnected(String, boolean)}, check that for docs and details */
    void updatePlayerConnected(String nick, boolean connected) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updatePlayerScore(String, int)}, check that for docs and details */
    void updatePlayerScore(String nick, int score) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateCurrentTurn(String)}, check that for docs and details */
    void updateCurrentTurn(String nick) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateFirstFinisher(String)}, check that for docs and details */
    void updateFirstFinisher(@Nullable String nick) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateEndGame(Boolean)}, check that for docs and details */
    void updateEndGame(Boolean endGame) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateSuspended(boolean)}, check that for docs and details */
    void updateSuspended(boolean suspended) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateMessage(UserMessage)}, check that for docs and details */
    void updateMessage(@Nullable UserMessage message) throws RemoteException;

    /** RMI redeclaration of {@link GameUpdater#updateAchievedCommonGoal(Type, List)}, check that for docs and details */
    void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws RemoteException;

    class Adapter extends RmiAdapter implements GameUpdater, Serializable {

        private final RmiGameUpdater updater;

        public Adapter(RmiGameUpdater updater) {
            this.updater = updater;
        }

        @Override
        public void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException {
            adapt(() -> updater.updateBoardTile(row, col, tile));
        }

        @Override
        public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException {
            adapt(() -> updater.updatePlayerShelfieTile(nick, row, col, tile));
        }

        @Override
        public void updatePlayerConnected(String nick, boolean connected) throws DisconnectedException {
            adapt(() -> updater.updatePlayerConnected(nick, connected));
        }

        @Override
        public void updatePlayerScore(String nick, int score) throws DisconnectedException {
            adapt(() -> updater.updatePlayerScore(nick, score));
        }

        @Override
        public void updateCurrentTurn(String nick) throws DisconnectedException {
            adapt(() -> updater.updateCurrentTurn(nick));
        }

        @Override
        public void updateFirstFinisher(@Nullable String nick) throws DisconnectedException {
            adapt(() -> updater.updateFirstFinisher(nick));
        }

        @Override
        public void updateEndGame(Boolean endGame) throws DisconnectedException {
            adapt(() -> updater.updateEndGame(endGame));
        }

        @Override
        public void updateSuspended(boolean suspended) throws DisconnectedException {
            adapt(() -> updater.updateSuspended(suspended));
        }

        @Override
        public void updateMessage(@Nullable UserMessage message) throws DisconnectedException {
            adapt(() -> updater.updateMessage(message));
        }

        @Override
        public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
            adapt(() -> updater.updateAchievedCommonGoal(commonGoalType, playersAchieved));
        }
    }
}
