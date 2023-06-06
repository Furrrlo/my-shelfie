package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.model.UserMessage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Object in charge of sending updates related to the {@link it.polimi.ingsw.model.Game} model class */
public interface GameUpdater {

    /**
     * Set the tile at the specified position on the board to the given one
     *
     * @param row row board position to update
     * @param col col board position to update
     * @param tile tile to set in the given position
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateBoardTile(int row, int col, @Nullable Tile tile) throws DisconnectedException;

    /**
     * Set the tile at the specified position on the shelfie of the requested player to the given one
     *
     * @param nick nick of the player whose shelfie is being changed
     * @param row row shelfie position to update
     * @param col col shelfie position to update
     * @param tile tile to set in the given position
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws DisconnectedException;

    /**
     * Set whether the specified player is still connected
     *
     * @param nick nick of the player whose connection state changed
     * @param connected false if the player disconnected
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updatePlayerConnected(String nick, boolean connected) throws DisconnectedException;

    /**
     * Set the score of the specified player
     *
     * @param nick nick of the player whose score is being set
     * @param score points that this player made
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updatePlayerScore(String nick, int score) throws DisconnectedException;

    /**
     * Set the player which should be playing the current turn
     *
     * @param nick nick of the player which should make a move next
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateCurrentTurn(String nick) throws DisconnectedException;

    /**
     * Set the player which finished filling its shelfie first
     *
     * @param nick nick of the player which finished filling its shelfie first
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateFirstFinisher(@Nullable String nick) throws DisconnectedException;

    /**
     * Sets whether this game has ended
     *
     * @param endGame true if this game is over
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateEndGame(Boolean endGame) throws DisconnectedException;

    /**
     * Sets whether this game is suspended because not enough players joined
     *
     * @param suspended true if the game is suspended
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateSuspended(boolean suspended) throws DisconnectedException;

    /**
     * Updates the last message sent to this player
     *
     * @param message message sent to this player
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateMessage(@Nullable UserMessage message) throws DisconnectedException;

    /**
     * Updates the list of players which has achieved the given common goal
     *
     * @param commonGoalType type used to identify the common goal instance
     * @param playersAchieved list of the players who have achieved the goal, in order of time
     * @throws DisconnectedException if the client disconnects during the update
     */
    void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws DisconnectedException;
}
