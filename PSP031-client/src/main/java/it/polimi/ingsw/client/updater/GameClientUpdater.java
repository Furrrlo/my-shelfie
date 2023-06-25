package it.polimi.ingsw.client.updater;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.updater.GameUpdater;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base client GameUpdater implementation which updates the game accordingly
 * based on the method calls it receives
 * <p>
 * This class must be extended by network implementations which need to call
 * the updaters method when the server sends notifications to do so.
 *
 * @see it.polimi.ingsw.controller
 */
public abstract class GameClientUpdater implements GameUpdater {

    public static final int MAX_MESSAGES_HISTORY = 100;

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
    public void updateBoardTile(int row, int col, @Nullable Tile tile) {
        Property.setNullable(game.getBoard().tile(row, col), tile);
    }

    @Override
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) {
        Property.setNullable(findPlayerBy(nick).getShelfie().tile(row, col), tile);
    }

    @Override
    public void updatePlayerConnected(String nick, boolean connected) {
        findPlayerBy(nick).connected().set(connected);
    }

    @Override
    public void updatePlayerScore(String nick, int score) {
        findPlayerBy(nick).score().set(score);
    }

    @Override
    public void updateCurrentTurn(String nick) {
        game.currentTurn().set(findPlayerBy(nick));
    }

    @Override
    public void updateFirstFinisher(@Nullable String nick) {
        Property.setNullable(game.firstFinisher(), nick == null ? null : findPlayerBy(nick));
    }

    @Override
    public void updateEndGame(Boolean endGame) {
        game.endGame().set(endGame);
    }

    @Override
    public void updateMessage(@Nullable UserMessage message) {
        var newMessageList = new ArrayList<>(game.messageList().get());
        if (newMessageList.size() >= MAX_MESSAGES_HISTORY)
            newMessageList.remove(0);
        newMessageList.add(message);
        game.messageList().set(Collections.unmodifiableList(newMessageList));
    }

    @Override
    public void updateSuspended(boolean suspended) {
        game.suspended().set(suspended);
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) {
        findCommonGoalBy(commonGoalType).achieved().set(playersAchieved.stream()
                .map(this::findPlayerBy)
                .collect(Collectors.toList()));
    }
}
