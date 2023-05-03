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

    @Test
    void numTilesOverlappingWithPersonalGoal() {
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.PINK     , Color.YELLOW, Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.WHITE    , Color.GREEN , Color.WHITE    , Color.WHITE    , Color.YELLOW }
                //@formatter:on
        });
        var pg1 = new PersonalGoal(0);
        //Num of overlapping tiles expected to be : 1
        assertEquals(1, shelfie.numTilesOverlappingWithPersonalGoal(pg1));

        var pg2 = new PersonalGoal(1);
        //Num of overlapping tiles expected to be : 1
        assertEquals(1, shelfie.numTilesOverlappingWithPersonalGoal(pg2));

        var pg3 = new PersonalGoal(2);
        //Num of overlapping tiles expected to be : 1
        assertEquals(1, shelfie.numTilesOverlappingWithPersonalGoal(pg3));

        var pg4 = new PersonalGoal(3);
        //Num of overlapping tiles expected to be : 2
        assertEquals(2, shelfie.numTilesOverlappingWithPersonalGoal(pg4));

        var pg5 = new PersonalGoal(4);
        //Num of overlapping tiles expected to be : 1
        assertEquals(1, shelfie.numTilesOverlappingWithPersonalGoal(pg5));

        var pg6 = new PersonalGoal(5);
        //Num of overlapping tiles expected to be : 0
        assertEquals(0, shelfie.numTilesOverlappingWithPersonalGoal(pg6));

        var pg7 = new PersonalGoal(6);
        //Num of overlapping tiles expected to be : 2
        assertEquals(2, shelfie.numTilesOverlappingWithPersonalGoal(pg7));

        var pg8 = new PersonalGoal(7);
        //Num of overlapping tiles expected to be : 0
        assertEquals(0, shelfie.numTilesOverlappingWithPersonalGoal(pg8));

        var pg9 = new PersonalGoal(8);
        //Num of overlapping tiles expected to be : 1
        assertEquals(1, shelfie.numTilesOverlappingWithPersonalGoal(pg9));

        var pg10 = new PersonalGoal(9);
        //Num of overlapping tiles expected to be : 3
        assertEquals(3, shelfie.numTilesOverlappingWithPersonalGoal(pg10));

        var pg11 = new PersonalGoal(10);
        //Num of overlapping tiles expected to be : 0
        assertEquals(0, shelfie.numTilesOverlappingWithPersonalGoal(pg11));

        var pg12 = new PersonalGoal(11);
        //Num of overlapping tiles expected to be : 4
        assertEquals(4, shelfie.numTilesOverlappingWithPersonalGoal(pg12));
    }
}