package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class TuiPrinterTest {
    private static final List<Tile> BAG = List.of(
            new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0),
            new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0), new Tile(Color.GREEN, 0),
            new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1),
            new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1), new Tile(Color.GREEN, 1),
            new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2),
            new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 2), new Tile(Color.GREEN, 0),
            new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0),
            new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0), new Tile(Color.BLUE, 0),
            new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1),
            new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1), new Tile(Color.BLUE, 1),
            new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2),
            new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 2), new Tile(Color.BLUE, 0),
            new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0),
            new Tile(Color.PINK, 0), new Tile(Color.PINK, 0), new Tile(Color.PINK, 0),
            new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1),
            new Tile(Color.PINK, 1), new Tile(Color.PINK, 1), new Tile(Color.PINK, 1),
            new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2),
            new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 2), new Tile(Color.PINK, 0),
            new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1), new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 1),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 2),
            new Tile(Color.LIGHTBLUE, 2), new Tile(Color.LIGHTBLUE, 0),
            new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0),
            new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0), new Tile(Color.YELLOW, 0),
            new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1),
            new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1), new Tile(Color.YELLOW, 1),
            new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2),
            new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 2), new Tile(Color.YELLOW, 0),
            new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0),
            new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0), new Tile(Color.WHITE, 0),
            new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1),
            new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1), new Tile(Color.WHITE, 1),
            new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2),
            new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 2), new Tile(Color.WHITE, 0));

    static TuiPrintStream out = TuiPrintStream.installToStdOut();

    @Test
    void PrintTileGreen() {
        TuiDetailedTilePrinter.of(Color.GREEN).print(out);
    }

    @Test
    void PrintTilePink() {
        TuiDetailedTilePrinter.of(Color.PINK).print(out);
    }

    @Test
    void PrintTileWhite() {
        TuiDetailedTilePrinter.of(Color.WHITE).print(out);
    }

    @Test
    void PrintTileLightBlue() {
        TuiDetailedTilePrinter.of(Color.LIGHTBLUE).print(out);
    }

    @Test
    void PrintTileYellow() {
        TuiDetailedTilePrinter.of(Color.YELLOW).print(out);
    }

    @Test
    void PrintTileBlue() {
        TuiDetailedTilePrinter.of(Color.BLUE).print(out);
    }

    @Test
    void tuiPrintShelfieFull() {
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.LIGHTBLUE, Color.BLUE     , Color.BLUE     , Color.LIGHTBLUE, Color.PINK      },
                new Color[] { Color.LIGHTBLUE, Color.WHITE    , Color.YELLOW   , Color.BLUE     , Color.BLUE      },
                new Color[] { null           , Color.WHITE    , Color.WHITE    , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.GREEN    , Color.BLUE     , null           , Color.WHITE    , Color.PINK      },
                new Color[] { Color.BLUE     , Color.WHITE    , Color.BLUE     , Color.YELLOW   , Color.GREEN     },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.YELLOW   , Color.BLUE     , Color.BLUE      },
                //@formatter:on
        });
        new TuiShelfiePrinter(shelfie).print(out);
    }

    @Test
    void tuiPrintShelfieRandom() {
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.WHITE    , Color.LIGHTBLUE, Color.YELLOW   , Color.WHITE    , Color.YELLOW    },
                new Color[] { Color.BLUE     , Color.WHITE    , Color.WHITE    , Color.BLUE     , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.PINK     , Color.PINK     , Color.YELLOW   , Color.YELLOW    },
                new Color[] { Color.LIGHTBLUE, Color.YELLOW   , Color.PINK     , Color.PINK     , Color.YELLOW    },
                new Color[] { Color.WHITE    , Color.WHITE    , Color.PINK     , Color.PINK     , Color.PINK      },
                new Color[] { Color.YELLOW   , Color.YELLOW   , Color.PINK     , Color.GREEN    , Color.PINK      },
                //@formatter:on
        });
        new TuiDetailedShelfiePrinter(shelfie).print(out);
    }

    @Test
    void tuiPrintEmptyShelfie() {
        Shelfie shelfie = new Shelfie(new Color[][] {
                //@formatter:off
                new Color[] { Color.GREEN    , null           , null           , null           , null            },
                new Color[] { Color.GREEN    , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                new Color[] { null           , null           , null           , null           , null            },
                //@formatter:on
        });
        new TuiDetailedShelfiePrinter(shelfie).print(out);
        out.println();
        new TuiDetailedPersonalGoalPrinter(new PersonalGoal(1)).print(out);
    }

    @Test
    void tuiPrintEmptyBoard() {
        new TuiDetailedBoardPrinter(new Board(4)).print(out);
    }

    @Test
    void tuiPrintBoardRandom() {
        try (var ignored = out.translateCursorToCol(20)) {
            new TuiDetailedBoardPrinter(Game.refillBoard(new Board(4), BAG)).print(out);
        }
    }

    @Test
    void printPersonalGoalRandom() {
        new TuiDetailedPersonalGoalPrinter(new PersonalGoal(1)).print(out);
    }
}