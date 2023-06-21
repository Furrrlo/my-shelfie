package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface GameView extends Serializable {

    // TODO: remember to bring back to 30 second
    Duration SUSPENDED_GAME_TIMEOUT = Duration.of(120, ChronoUnit.SECONDS);

    /** Returns game ID */
    int getGameID();

    /** Returns playing board */
    BoardView getBoard();

    /** Returns list of players */
    @Unmodifiable
    List<? extends PlayerView> getPlayers();

    /** Returns list of players, sorted by score */
    @Unmodifiable
    List<? extends PlayerView> getSortedPlayers();

    /** Returns this client's player */
    PlayerView thePlayer();

    /** Returns the player who has started his turn first */
    PlayerView getStartingPlayer();

    /** Return player whose now playing */
    Provider<? extends PlayerView> currentTurn();

    /** Return personal goal of the player */
    PersonalGoalView getPersonalGoal();

    /** Return list of common goals of the game */
    @Unmodifiable
    List<? extends CommonGoalView> getCommonGoals();

    /** Return first player completing his shelfie */
    Provider<? extends @Nullable PlayerView> firstFinisher();

    /** Returns endGame */
    Provider<Boolean> endGame();

    Provider<? extends List<UserMessage>> messageList();

    /** Returns whether the game is suspended */
    Provider<Boolean> suspended();
}
