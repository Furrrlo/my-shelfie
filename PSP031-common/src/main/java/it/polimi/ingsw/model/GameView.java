package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface GameView extends Serializable {

    /**
     * @return game ID
     */
    int getGameID();

    /**
     * @return playing board
     */
    BoardView getBoard();

    /**
     * @return list of players
     */
    List<? extends PlayerView> getPlayers();

    /**
     * @return player whose now playing
     */
    Provider<? extends PlayerView> currentTurn();

    /**
     * @return personal goal of the player
     */
    PersonalGoalView getPersonalGoal();

    /**
     * @return list of common goals of the game
     */
    List<? extends CommonGoalView> getCommonGoals();

    /**
     * @return first player completing his shelfie
     */
    Provider<? extends @Nullable PlayerView> firstFinisher();
}
