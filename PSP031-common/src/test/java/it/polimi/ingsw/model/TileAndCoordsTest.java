package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileAndCoordsTest {
    @Test
    void nullable() {
        final var tile = TileAndCoords.nullable(new Tile(Color.GREEN), 1, 1);
        final var nullTile = TileAndCoords.nullable(null, 1, 1);
        assertEquals(tile.tile(), new Tile(Color.GREEN));
        assertNull(nullTile.tile());
    }
}