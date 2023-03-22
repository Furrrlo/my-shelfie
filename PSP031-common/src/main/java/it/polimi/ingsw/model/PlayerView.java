package it.polimi.ingsw.model;

import java.io.Serializable;

public interface PlayerView extends Serializable {

    /**
     * @return player's nick
     */
    String getNick();

    /**
     * @return shelfie as matrix of tiles
     */
    ShelfieView getShelfie();
}
