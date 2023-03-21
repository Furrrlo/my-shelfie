package it.polimi.ingsw.model;

public interface BoardView {

    int getRows();

    int getCols();

    Provider<Tile> tile(int r, int c);
}
