package it.polimi.ingsw.model;

public interface PlayerView {
    /**
     * @return player's nick
     */
    String getNick();

    /**
     * @return shelfie as matrix of tiles
     */
    ShelfieView getShelfie();

}
