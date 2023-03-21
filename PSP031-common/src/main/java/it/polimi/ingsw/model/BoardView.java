package it.polimi.ingsw.model;

public interface BoardView {
    /**
     * @param r
     * @param c
     * @return
     */
    Provider<Tile> tile(int r, int c);
}
