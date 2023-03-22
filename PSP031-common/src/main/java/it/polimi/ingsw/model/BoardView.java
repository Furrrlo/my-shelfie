package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.stream.Stream;

public interface BoardView extends Serializable {

    int getRows();

    int getCols();

    Provider<Tile> tile(int r, int c);

    Stream<? extends TileAndCoords<? extends Provider<Tile>>> tiles();
}
