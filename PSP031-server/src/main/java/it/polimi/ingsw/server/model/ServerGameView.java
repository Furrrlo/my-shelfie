package it.polimi.ingsw.server.model;

import java.util.List;

import it.polimi.ingsw.model.*;

public interface ServerGameView {
    /**
     * @return list of players
     */
    List<? extends ServerPlayerView> getPlayers();

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
    Provider<? extends ServerPlayerView> firstFinisher();

    /**
     * @return player whose now playing
     */
    Provider<? extends ServerPlayerView> currentTurn();

    /**
     * @return list tiles in the bag
     */
    List<Tile> getBag();
}
