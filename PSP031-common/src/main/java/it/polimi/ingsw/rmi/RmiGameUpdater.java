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

public interface RmiGameUpdater extends Remote {

    void updateBoardTile(int row, int col, @Nullable Tile tile) throws RemoteException;

    void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws RemoteException;

    void updatePlayerConnected(String nick, boolean connected) throws RemoteException;

    void updatePlayerScore(String nick, int score) throws RemoteException;

    void updateCurrentTurn(String nick) throws RemoteException;

    void updateFirstFinisher(@Nullable String nick) throws RemoteException;

    void updateEndGame(Boolean endGame) throws RemoteException;

    void updateSuspended(boolean suspended) throws RemoteException;

    void updateMessage(UserMessage message) throws RemoteException;

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
        public void updateMessage(UserMessage message) throws DisconnectedException {
            adapt(() -> updater.updateMessage(message));
        }

        @Override
        public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
            adapt(() -> updater.updateAchievedCommonGoal(commonGoalType, playersAchieved));
        }
    }
}
