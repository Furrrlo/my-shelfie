package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void getColor0() {
        assertEquals(new Tile(Color.BLUE).getColor(), Color.BLUE);
    }

    @Test
    void getColor1() {
        assertNotEquals(new Tile(Color.BLUE).getColor(), Color.YELLOW);
    }

    @Test
    void testEquals() {
        Tile t1 = new Tile(Color.BLUE);
        Tile t2 = new Tile(Color.BLUE);
        assertEquals(t1, t2);
    }

    @Test
    void getPicIndex() {
        Tile t1 = new Tile(Color.BLUE);
        assertTrue(t1.getPicIndex() >= 0 && t1.getPicIndex() <= 3);
    }
}