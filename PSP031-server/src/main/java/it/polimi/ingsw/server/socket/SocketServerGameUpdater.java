package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.model.UserMessage;
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
    public void updatePlayerConnected(String nick, boolean connected) throws DisconnectedException {
        try {
            socketManager.send(new UpdatePlayerConnectedPacket(nick, connected));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updatePlayerScore(String nick, int score) throws DisconnectedException {
        try {
            socketManager.send(new UpdatePlayerScorePacket(nick, score));
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
    public void updateFirstFinisher(@Nullable String nick) throws DisconnectedException {
        try {
            socketManager.send(new UpdateFirstFinisherPacket(nick));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateEndGame(Boolean endGame) throws DisconnectedException {
        try {
            socketManager.send(new UpdateEndGamePacket(endGame));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateSuspended(boolean suspended) throws DisconnectedException {
        try {
            socketManager.send(new UpdateSuspendedPacket(suspended));
        } catch (IOException e) {
            throw new DisconnectedException(e);
        }
    }

    @Override
    public void updateMessage(@Nullable UserMessage message) throws DisconnectedException {
        try {
            socketManager.send(new UpdateMessagePacket(message));
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
