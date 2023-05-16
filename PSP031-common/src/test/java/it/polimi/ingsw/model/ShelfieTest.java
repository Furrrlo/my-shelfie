package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.List;

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
    void checkColumnSpace() {
        final var shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { null           , null        , null           , Color.WHITE    , Color.YELLOW },
                new Color[] { null           , null        , Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.BLUE     , null        , Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
                //@formatter:on
        });
        //first column: should return true for group of 1 and 2 tiles, but false for three 
        assertTrue(shelfie.checkColumnSpace(0, 1));
        assertTrue(shelfie.checkColumnSpace(0, 2));
        assertFalse(shelfie.checkColumnSpace(0, 3));
        //second columns: should always return true 
        assertTrue(shelfie.checkColumnSpace(1, 1));
        assertTrue(shelfie.checkColumnSpace(1, 2));
        assertTrue(shelfie.checkColumnSpace(1, 3));
        //third column: should return true only for groups of single tile 
        assertTrue(shelfie.checkColumnSpace(2, 1));
        assertFalse(shelfie.checkColumnSpace(2, 2));
        assertFalse(shelfie.checkColumnSpace(2, 3));
        //fourth column: should always return false 
        assertFalse(shelfie.checkColumnSpace(3, 1));
        assertFalse(shelfie.checkColumnSpace(3, 2));
        assertFalse(shelfie.checkColumnSpace(3, 3));
        //fifth column: should always return false 
        assertFalse(shelfie.checkColumnSpace(4, 1));
        assertFalse(shelfie.checkColumnSpace(4, 2));
        assertFalse(shelfie.checkColumnSpace(4, 3));
    }

    @Test
    void isFull() {
        final var shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { null           , null        , null           , Color.WHITE    , Color.YELLOW },
                new Color[] { null           , null        , Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.BLUE     , null        , Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
                //@formatter:on
        });
        assertFalse(shelfie.isFull());
        Shelfie shelfie1 = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.WHITE    , Color.GREEN , Color.WHITE    , Color.WHITE    , Color.YELLOW },
                new Color[] { Color.PINK     , Color.YELLOW, Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
                //@formatter:on
        });
        assertTrue(shelfie1.isFull());
    }

    @Test
    void placeTiles() {
        final var shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { null           , null        , null           , Color.WHITE    , Color.YELLOW },
                new Color[] { null           , null        , Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.BLUE     , null        , Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
                //@formatter:on
        });
        final var selected = List.of(new Tile(Color.GREEN), new Tile(Color.BLUE));
        shelfie.placeTiles(selected, 0);
        assertEquals(new Tile(Color.GREEN), shelfie.tile(1, 0).get());
        assertEquals(new Tile(Color.BLUE), shelfie.tile(0, 0).get());
        assertNotEquals(new Tile(Color.BLUE), shelfie.tile(1, 0).get());
        assertNotEquals(new Tile(Color.GREEN), shelfie.tile(0, 0).get());
    }

    @Test
    void numTilesOverlappingWithPersonalGoal() {
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.WHITE    , Color.GREEN , Color.WHITE    , Color.WHITE    , Color.YELLOW },
                new Color[] { Color.PINK     , Color.YELLOW, Color.YELLOW   , Color.YELLOW   , Color.GREEN  },
                new Color[] { Color.BLUE     , Color.YELLOW, Color.BLUE     , Color.BLUE     , Color.BLUE   },
                new Color[] { Color.LIGHTBLUE, Color.WHITE , Color.GREEN    , Color.LIGHTBLUE, Color.WHITE  },
                new Color[] { Color.GREEN    , Color.BLUE  , Color.YELLOW   , Color.GREEN    , Color.YELLOW },
                new Color[] { Color.YELLOW   , Color.BLUE  , Color.YELLOW   , Color.PINK     , Color.YELLOW },
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

    @Test
    void groupsOfTiles() {
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
        /*
         * 1 -> yellow
         * 2 -> blue, blue
         * 3 -> yellow, yellow
         * 4 -> pink
         * 5 -> yellow, yellow
         * 6 -> green
         * 7 -> green
         * 8 -> lightblue
         * 9 -> white
         * 10 -> green
         * 11 -> lightblue
         * 12 -> white
         * 13 -> blue
         * 14 -> yellow, yellow, yellow, yellow
         * 15 -> blue, blue, blue
         * 16 -> pink
         * 17 -> green
         * 18 -> white
         * 19 -> green
         * 20 -> white, white
         * 21 -> yellow
         */
        assertEquals(21, shelfie.groupsOfTiles().size());

        Shelfie shelfie2 = new Shelfie(new Color[][] {
        //@formatter:off
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.PINK      },
                new Color[] { Color.PINK     , Color.PINK     , Color.BLUE     , Color.BLUE     , Color.PINK      },
                new Color[] { Color.PINK     , Color.PINK     , Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.GREEN    , Color.GREEN    , Color.WHITE     },
                new Color[] { Color.LIGHTBLUE, Color.LIGHTBLUE, Color.GREEN    , Color.GREEN    , Color.PINK      },
        //@formatter:on
        });
        /*
         * 1 -> pink x 4
         * 2 -> lightblue x 6
         * 3 -> lightblue x 2
         * 4 -> green x 4
         * 5 -> yellow x 5
         * 6 -> white
         * 7 -> pink
         * 8 -> blue x 2
         * 9 -> yellow x 2
         * 10 -> pink x 2
         * 11 -> lightblue
         */
        assertEquals(11, shelfie2.groupsOfTiles().size());
    }
}