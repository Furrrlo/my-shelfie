package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.stream.Stream;

public interface ShelfieView extends Serializable {

    int ROWS = 6;
    int COLUMNS = 5;

    /**
     * Returns the tile in position r & c
     * 
     * @param r defines row of shelfie
     * @param c defines column of shelfie
     * @return tile in position r & c
     */
    Provider<@Nullable Tile> tile(int r, int c);

    Stream<? extends TileAndCoords<? extends Provider<@Nullable Tile>>> tiles();
}
