package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public record TileAndCoords<P>(P tile, int row, int col) implements Coord, Serializable {

    @SuppressWarnings("NullAway") // NullAway doesn't support generics properly yet
    public static <P> TileAndCoords<@Nullable P> nullable(@Nullable P tile, int row, int col) {
        return new TileAndCoords<>(tile, row, col);
    }
}
