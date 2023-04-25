package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShelfieTest {

    @Test
    void tiles() {
        assertDoesNotThrow(() -> new Shelfie().tiles().forEach(t -> {
        }));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var shelfie1 = new Shelfie();
        final var shelfie2 = new Shelfie();

        assertEquals(shelfie1, shelfie1, "Same instance is not the same");
        assertNotEquals(shelfie1, new Object(), "Different object should not be equals");
        assertEquals(shelfie1, shelfie2, "Instances with no differences should be equals");

        final var shelfieDiffTile = new Shelfie();
        shelfieDiffTile.tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(shelfie1, shelfieDiffTile, "Instances with different tiles should not be equals");
    }

    @Test
    void testHashCode() {
        final var shelfie1 = new Shelfie();
        final var shelfie2 = new Shelfie();

        assertEquals(shelfie1.hashCode(), shelfie1.hashCode(), "Same instance is not the same");
        assertEquals(shelfie1.hashCode(), shelfie2.hashCode(), "Instances with no differences should be equals");

        final var shelfieDiffTile = new Shelfie();
        shelfieDiffTile.tile(0, 0).set(new Tile(Color.BLUE));
        assertNotEquals(shelfie1.hashCode(), shelfieDiffTile.hashCode(), "Instances with different tiles should not be equals");
    }

    @Test
    void testToString() {
        final var shelfie = new Shelfie();
        shelfie.tile(ShelfieView.ROWS / 2, ShelfieView.COLUMNS / 2).set(new Tile(Color.BLUE));
        assertDoesNotThrow(shelfie::toString);
    }
}