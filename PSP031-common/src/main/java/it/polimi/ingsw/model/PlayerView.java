package it.polimi.ingsw.model;

import java.io.Serializable;

public interface PlayerView extends Serializable {

    /** Return player's nick */
    String getNick();

    /** Return shelfie as matrix of tiles */
    ShelfieView getShelfie();

    /** Returns true if this is the player who has started his turn first */
    boolean isStartingPlayer();

    /** Returns true if this player is still connected to the server */
    Provider<Boolean> connected();

    /** Returns true if this is the player whose now playing */
    Provider<Boolean> isCurrentTurn();

    /** Returns true if this is the player who has finished his shelfie first */
    Provider<Boolean> isFirstFinisher();

    /** Returns the player's score */
    Provider<Integer> score();
}
