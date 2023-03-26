package it.polimi.ingsw.client.updater;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.updater.GameUpdater;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GameClientUpdater implements GameUpdater {

    protected final Game game;

    public GameClientUpdater(Game game) {
        this.game = game;
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
