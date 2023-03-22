package it.polimi.ingsw.model;

import java.io.Serializable;

public interface ShelfieView extends Serializable {

    int ROWS = 6;
    int COLUMNS = 5;

    /**
     * @param r defines row of shelfie
     * @param c defines column of shelfie
     * @return tile in position r & c
     */
    Provider<Tile> tile(int r, int c);
}
