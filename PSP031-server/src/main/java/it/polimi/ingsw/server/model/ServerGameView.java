package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.BoardView;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ServerGameView {

    /**
     * @return game ID
     */
    int getGameID();

    /**
     * @return playing board
     */
    BoardView getBoard();

    /**
     * @return list tiles in the bag
     */
    @UnmodifiableView
    List<Tile> getBagView();

    /**
     * @return list of players
     */
    @Unmodifiable
    List<? extends ServerPlayerView> getPlayers();

    /**
     * @return player whose now playing
     */
    Provider<? extends ServerPlayerView> currentTurn();

    /**
     * @return list of common goals of the game
     */
    @Unmodifiable
    List<? extends ServerCommonGoalView> getCommonGoals();

    /**
     * @return first player compleating his shelfie
     */
    Provider<? extends ServerPlayerView> firstFinisher();
}
