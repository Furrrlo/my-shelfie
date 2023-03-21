package it.polimi.ingsw.server.model;


public interface ShelfieView {
    /**
     * @param r defines row of shelfie
     * @param c defines column of shelfie
     * @return tile in position r & c
     */
    Provider<Tile> tile(int r, int c);
}
