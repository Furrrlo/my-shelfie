package it.polimi.ingsw.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyView;

/** Controller that allows a player to set parameters/settings in the lobby */
public interface LobbyController {

    /**
     * Set the required amount of players this game needs to start
     * <p>
     * If the {@code requiredPlayers} params is set to 0, then in means that the server will start the game
     * as soon as there are more than 2 players and all the ones in the lobby are ready.
     * <p>
     * This method can only be called by the creator of the lobby. The server assumes the check is also
     * done by the client and will therefore kick anyone else attempting to call this when not allowed.
     *
     * @param requiredPlayers num of required players.
     *        Must be 0 or between {@link LobbyView#MIN_PLAYERS} and {@link LobbyView#MAX_PLAYERS}
     * @throws DisconnectedException if the connection is lost during the call
     */
    void setRequiredPlayers(int requiredPlayers) throws DisconnectedException;

    /**
     * Set whether this player is ready to start the game or not
     * <p>
     * The game will start only when at least {@link Lobby#requiredPlayers()} number
     * of players are ready.
     *
     * @param ready true if this player is ready to start the game
     * @throws DisconnectedException if the connection is lost during the call
     */
    void ready(boolean ready) throws DisconnectedException;
}
