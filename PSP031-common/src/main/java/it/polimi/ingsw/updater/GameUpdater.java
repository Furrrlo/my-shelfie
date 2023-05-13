package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GameUpdater {

    void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException;

    void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException;

    void updatePlayerConnected(String nick, boolean connected) throws DisconnectedException;

    void updatePlayerScore(String nick, int score) throws DisconnectedException;

    void updateCurrentTurn(String nick) throws DisconnectedException;

    void updateFirstFinisher(@Nullable String nick) throws DisconnectedException;

    void updateEndGame(Boolean endGame) throws DisconnectedException;

    void updateSuspended(boolean suspended) throws DisconnectedException;

    void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException;
}
