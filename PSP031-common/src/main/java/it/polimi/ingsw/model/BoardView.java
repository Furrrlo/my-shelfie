package it.polimi.ingsw.model;

import java.io.Serializable;

public interface BoardView extends Serializable {

    int getRows();

    int getCols();

    Provider<Tile> tile(int r, int c);
}
