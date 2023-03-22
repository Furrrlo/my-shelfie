package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RmiGameUpdater extends Remote {

    void updateBoardTile(int row, int col, @Nullable Tile tile) throws RemoteException;

    void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws RemoteException;

    void updateCurrentTurn(String nick) throws RemoteException;

    void updateFirstFinisher(String nick) throws RemoteException;

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
        public void updateCurrentTurn(String nick) throws DisconnectedException {
            adapt(() -> updater.updateCurrentTurn(nick));
        }

        @Override
        public void updateFirstFinisher(String nick) throws DisconnectedException {
            adapt(() -> updater.updateFirstFinisher(nick));
        }

        @Override
        public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
            adapt(() -> updater.updateAchievedCommonGoal(commonGoalType, playersAchieved));
        }
    }
}
