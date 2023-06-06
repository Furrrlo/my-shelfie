package it.polimi.ingsw.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.UserMessage;

import java.util.List;

/** Controller that allows a player to make actions while a game is in progress */
public interface GameController {

    /**
     * Makes the player perform a move
     * <p>
     * A move is defined as both picking the tiles from the game board and putting them inside your own shelfie at a
     * specific column.
     * <p>
     * The order with which tiles are put in the shelfie is represented by the order of the {@code selected} list,
     * e.g. if selected is {(5, 5), (7, 8)} then (5, 5) will be the first put inside the shelfie, therefore it
     * will go in the lowest possible free space in the shelfie column.
     * <p>
     * This method assumes the client already performed all the necessary checks to make sure the player can make a
     * move and that the move itself is valid; it will therefore kick the player off of the server in case this is not
     * the case.
     *
     * @param selected board coordinates of the tiles to pick, ordered by how they need to be put in the shelfie
     * @param shelfCol column of the shelfie where the tiles need to be places
     * @throws DisconnectedException if the connection is lost during the call
     */
    void makeMove(List<BoardCoord> selected, int shelfCol) throws DisconnectedException;

    /**
     * Sends a message to the specified recipient
     * <p>
     * The recipient can be:
     * - a player's nick, in which case a private message will be sent to him,
     * - {@link UserMessage#EVERYONE_RECIPIENT} which will send a message visible to every other player in this game
     *
     * @param message the message to be sent
     * @param nickReceivingPlayer the recipient (either a player's nick or {@link UserMessage#EVERYONE_RECIPIENT}
     * @throws DisconnectedException if the connection is lost during the call
     */
    void sendMessage(String message, String nickReceivingPlayer) throws DisconnectedException;
}
