package it.polimi.ingsw.model;

import java.util.List;

public interface GameView {
    /**
     * @return list of players
     */
    List<? extends PlayerView> getPlayers();

    /**
     * @return playing board
     */
    BoardView getBoard();

    /**
     * @return list of common goals of the game
     */
    List<? extends CommonGoalView> getCommonGoal();

    /**
     * @return game ID
     */
    int getGameID();

    /**
     * @return first player compleating his shelfie
     */
    Provider<? extends PlayerView> firstFinisher();

    /**
     * @return player whose now playing
     */
    Provider<? extends PlayerView> currentTurn();

    /**
     * @return personal goal of the player
     */
    PersonalGoalView getPersonalGoal();
}
