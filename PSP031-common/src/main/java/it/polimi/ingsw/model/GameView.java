package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface GameView extends Serializable {

    /** Returns game ID */
    int getGameID();

    /** Returns playing board */
    BoardView getBoard();

    /** Returns list of players */
    List<? extends PlayerView> getPlayers();

    /** Return player whose now playing */
    Provider<? extends PlayerView> currentTurn();

    /** Return personal goal of the player */
    PersonalGoalView getPersonalGoal();

    /** Return list of common goals of the game */
    List<? extends CommonGoalView> getCommonGoals();

    /** Return first player completing his shelfie */
    Provider<? extends @Nullable PlayerView> firstFinisher();
}
