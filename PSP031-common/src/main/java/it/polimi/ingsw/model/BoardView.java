package it.polimi.ingsw.model;

public interface BoardView {

    Provider<Tile> tile(int r, int c);
}
