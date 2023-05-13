package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.socket.packets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;

public class SocketGameClientUpdater extends GameClientUpdater implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketGameClientUpdater.class);

    private final ClientSocketManager socketManager;

    public SocketGameClientUpdater(Game game, ClientSocketManager socketManager) {
        super(game);
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        LOGGER.info("[Client] Game Updater started");
        try {
            do {
                try (var ctx = socketManager.receive(GameUpdaterPacket.class)) {
                    switch (ctx.getPacket()) {
                        case UpdateBoardTilePacket packet ->
                            updateBoardTile(packet.row(), packet.col(), packet.tile());
                        case UpdatePlayerShelfieTilePacket packet ->
                            updatePlayerShelfieTile(packet.nick(), packet.row(), packet.col(), packet.tile());
                        case UpdateCurrentTurnPacket packet ->
                            updateCurrentTurn(packet.nick());
                        case UpdateFirstFinisherPacket packet ->
                            updateFirstFinisher(packet.nick());
                        case UpdateAchievedCommonGoalPacket packet ->
                            updateAchievedCommonGoal(packet.commonGoalType(), packet.playersAchieved());
                        case UpdatePlayerConnectedPacket packet ->
                            updatePlayerConnected(packet.nick(), packet.connected());
                        case UpdatePlayerScorePacket packet ->
                            updatePlayerScore(packet.nick(), packet.score());
                        case UpdateEndGamePacket packet ->
                            updateEndGame(packet.endGame());
                        case UpdateSuspendedPacket packet ->
                            updateSuspended(packet.suspended());
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
