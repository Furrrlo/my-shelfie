package it.polimi.ingsw.model;

import java.io.Serializable;

public interface PlayerView extends Serializable {

    /** Return player's nick */
    String getNick();

    /** Return shelfie as matrix of tiles */
    ShelfieView getShelfie();
}
