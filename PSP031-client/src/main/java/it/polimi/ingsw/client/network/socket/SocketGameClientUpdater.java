package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.packets.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;

public class SocketGameClientUpdater extends GameClientUpdater implements Runnable {

    private final ClientSocketManager socketManager;

    public SocketGameClientUpdater(Game game, ClientSocketManager socketManager) {
        super(game);
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        System.out.println("[CLient] Game Updater started");
        try {
            do {
                try (var ctx = socketManager.receive(GameUpdaterPacket.class)) {
                    final GameUpdaterPacket p = ctx.getPacket();
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
                    } else {
                        throw new IOException("Received unexpected packet " + p);
                    }
                }
            } while (!Thread.interrupted());
        } catch (InterruptedIOException ignored) {
            // We got interrupted, normal flow
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
