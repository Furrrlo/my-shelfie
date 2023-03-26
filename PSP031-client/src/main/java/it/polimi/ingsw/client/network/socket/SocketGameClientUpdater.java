package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.packets.*;

import java.io.IOException;

public class SocketGameClientUpdater extends GameClientUpdater implements Runnable {

    private final ClientSocketManager socketManager;

    public SocketGameClientUpdater(Game game, ClientSocketManager socketManager) {
        super(game);
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        do {
            System.out.println("[CLient] Game Updater started");
            try (var ctx = socketManager.receive(S2CPacket.class)) {

                final S2CPacket p = ctx.getPacket();
                if (p instanceof final UpdateBoardTilePacket packet) {
                    updateBoardTile(packet.row(), packet.col(), packet.tile());
                } else if (p instanceof final UpdatePlayerShelfieTilePacket packet) {
                    updatePlayerShelfieTile(packet.nick(), packet.row(), packet.col(), packet.tile());
                } else if (p instanceof final UpdateCurrentTurnPacket packet) {
                    updateCurrentTurn(packet.nick());
                } else if (p instanceof final UpdateFirstFinisherPacket packet) {
                    updateFirstFinisher(packet.nick());
                } else if (p instanceof final UpdateAchievedCommonGoalPacket packet) {
                    updateAchievedCommonGoal(packet.commonGoalType(), packet.playersAchieved());
                }
            } catch (IOException e) {
                throw new RuntimeException(e); //TODO:
            }
        } while (!Thread.interrupted());
    }
}
