package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.socket.packets.*;
import it.polimi.ingsw.updater.GameUpdater;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SocketGameClientUpdater implements GameUpdater, Runnable {

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

    private Player findPlayerBy(String nick) {
        return game.getPlayers().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow();
    }

    private CommonGoal findCommonGoalBy(Type commonGoalType) {
        return game.getCommonGoals().stream()
                .filter(goal -> goal.getType() == commonGoalType)
                .findFirst()
                .orElseThrow();
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't handle generics correctly
    public void updateBoardTile(int row, int col, @Nullable Tile tile) {
        game.getBoard().tile(row, col).set(tile);
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't handle generics correctly
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) {
        findPlayerBy(nick).getShelfie().tile(row, col).set(tile);
    }

    @Override
    public void updateCurrentTurn(String nick) {
        game.currentTurn().set(findPlayerBy(nick));
    }

    @Override
    public void updateFirstFinisher(String nick) {
        game.firstFinisher().set(findPlayerBy(nick));
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) {
        findCommonGoalBy(commonGoalType).achieved().set(playersAchieved.stream()
                .map(this::findPlayerBy)
                .collect(Collectors.toList()));
    }
}
