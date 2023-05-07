package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.BoardView;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ServerGameView {

    /** Returns the game ID */
    int getGameID();

    /** Returns the playing board */
    BoardView getBoard();

    /** Returns the list tiles in the bag */
    @UnmodifiableView
    List<Tile> getBagView();

    /** Returns the list of players */
    @Unmodifiable
    List<? extends ServerPlayerView> getPlayers();

    /** Returns the player who has started his turn first */
    ServerPlayerView getStartingPlayer();

    /** Returns the player whose now playing */
    Provider<? extends ServerPlayerView> currentTurn();

    /** Returns the list of common goals of the game */
    @Unmodifiable
    List<? extends ServerCommonGoalView> getCommonGoals();

    /** Return first player completing his shelfie */
    Provider<? extends @Nullable ServerPlayerView> firstFinisher();

    /** Returns boolean endGame */
    Provider<? extends Boolean> endGame();

    /** Returns true if conditions for endGame are achieved */
    boolean isEndGame();
}
