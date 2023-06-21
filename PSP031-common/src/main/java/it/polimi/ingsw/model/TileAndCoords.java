package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * A tile with its corresponding cell coordinates in either a shelfie or the board
 * 
 * @param tile the tile
 * @param row the row coord of this tile
 * @param col the col coord of this tile
 * @param <P> the type of this tile.
 *        This has no lower boundary as it can be either a {@code Property<Tile>} or a Tile
 */
public record TileAndCoords<P>(P tile, int row, int col) implements Coord, Serializable {

    /**
     * Workaround to make NullAway work properly with generics
     * 
     * @see TileAndCoords#TileAndCoords(Object, int, int)
     */
    @SuppressWarnings("NullAway") // NullAway doesn't support generics properly yet
    public static <P> TileAndCoords<@Nullable P> nullable(@Nullable P tile, int row, int col) {
        return new TileAndCoords<>(tile, row, col);
    }
}
