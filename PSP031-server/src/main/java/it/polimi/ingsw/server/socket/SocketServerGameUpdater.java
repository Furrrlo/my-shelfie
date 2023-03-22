package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.socket.SocketGameUpdater;
import org.jetbrains.annotations.Nullable;

import java.io.ObjectOutputStream;
import java.util.List;

public class SocketServerGameUpdater implements SocketGameUpdater {
    private final ObjectOutputStream oos;

    public SocketServerGameUpdater(ObjectOutputStream oos) {
        this.oos = oos;
    }

    @Override
    public void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException {
        //TODO: send packet
    }

    @Override
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException {
        //TODO: send packet
    }

    @Override
    public void updateCurrentTurn(String nick) throws DisconnectedException {
        //TODO: send packet
    }

    @Override
    public void updateFirstFinisher(String nick) throws DisconnectedException {
        //TODO: send packet
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
        //TODO: send packet
    }
}
