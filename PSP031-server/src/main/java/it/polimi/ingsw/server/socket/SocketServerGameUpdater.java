package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.socket.packets.*;
import it.polimi.ingsw.updater.GameUpdater;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class SocketServerGameUpdater implements GameUpdater {
    private final ServerSocketManager socketManager;

    public SocketServerGameUpdater(ServerSocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException {
        try {
            socketManager.send(new UpdateBoardTilePacket(row, col, tile));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException {
        try {
            socketManager.send(new UpdatePlayerShelfieTilePacket(nick, row, col, tile));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateCurrentTurn(String nick) throws DisconnectedException {
        try {
            socketManager.send(new UpdateCurrentTurnPacket(nick));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateFirstFinisher(String nick) throws DisconnectedException {
        try {
            socketManager.send(new UpdateFirstFinisherPacket(nick));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
        try {
            socketManager.send(new UpdateAchievedCommonGoalPacket(commonGoalType, playersAchieved));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }
}
