package it.polimi.ingsw.model;

public interface ShelfieView {

    int ROWS = 6;
    int COLUMNS = 5;

    /**
     * @param r defines row of shelfie
     * @param c defines column of shelfie
     * @return tile in position r & c
     */
    Provider<Tile> tile(int r, int c);
}
