package it.polimi.ingsw.model;

import java.io.Serializable;

/** Read-only object which represent a player as seen in a lobby */
public interface LobbyPlayerView extends Serializable {

    /** Returns the nickname of this player */
    String getNick();

    /** Whether this player is ready to start the game */
    Provider<Boolean> ready();
}
