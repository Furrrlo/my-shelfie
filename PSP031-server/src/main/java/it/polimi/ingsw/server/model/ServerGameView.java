package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.BoardView;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.UserMessage;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Read-only object which represent an in-progress game, as seen by the server.
 * <p>
 * Compared to the client, the server can see:
 * - the list of tiles still in the bag {@link #getBagView()}
 * In addition, it returns {@link ServerPlayerView} and {@link ServerCommonGoalView} instead of
 * {@link it.polimi.ingsw.model.PlayerView} and {@link it.polimi.ingsw.model.CommonGoalView} instances.
 *
 * @see it.polimi.ingsw.model.GameView
 */
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
    Provider<Boolean> endGame();

    Provider<? extends @Nullable UserMessage> message();

    /** Returns whether the game is suspended */
    Provider<Boolean> suspended();
}
