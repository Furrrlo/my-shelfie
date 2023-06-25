package it.polimi.ingsw.model;

import java.io.Serializable;

public interface LobbyPlayerView extends Serializable {
    String getNick();

    /** Whether this player is ready to start the game */
    Provider<Boolean> ready();
}
