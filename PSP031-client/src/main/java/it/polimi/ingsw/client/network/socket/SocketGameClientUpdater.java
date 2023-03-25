package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.socket.SocketGameUpdater;
import it.polimi.ingsw.socket.packets.S2CPacket;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class SocketGameClientUpdater implements SocketGameUpdater, Runnable {

    private final Game game;
    private final ClientSocketManager socketManager;

    public SocketGameClientUpdater(Game game, ClientSocketManager socketManager) {
        this.game = game;
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        do {
            System.out.println("[CLient] Game Updater started");
            try (var ctx = socketManager.receive(S2CPacket.class)) {

                final S2CPacket p = ctx.getPacket();
                //TODO: call the correct method
            } catch (IOException e) {
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
