package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.socket.SocketGameUpdater;
import it.polimi.ingsw.socket.packets.S2CPacket;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class SocketGameClientUpdater implements SocketGameUpdater, Runnable {

    private final Game game;
    private final ObjectInputStream ois;

    public SocketGameClientUpdater(Game game, ObjectInputStream ois) {
        this.game = game;
        this.ois = ois;
    }

    @Override
    public void run() {
        do {
            try {
                final S2CPacket p = (S2CPacket) ois.readObject();
                //TODO: call the correct method
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e); //TODO:
            }
        } while (!Thread.interrupted());
    }

    @Override
    public void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException {
        //TODO
    }

    @Override
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException {
        //TODO
    }

    @Override
    public void updateCurrentTurn(String nick) throws DisconnectedException {
        //TODO
    }

    @Override
    public void updateFirstFinisher(String nick) throws DisconnectedException {
        //TODO
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException {
        //TODO
    }
}
