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

    @Test
    void PrintTileGreen() {
        TuiPrinter.tuiPrintTile(Color.GREEN);
    }

    @Test
    void PrintTilePink() {
        TuiPrinter.tuiPrintTile(Color.PINK);
    }

    @Test
    void PrintTileWhite() {
        TuiPrinter.tuiPrintTile(Color.WHITE);
    }

    @Test
    void PrintTileLightBlue() {
        TuiPrinter.tuiPrintTile(Color.LIGHTBLUE);
    }

    @Test
    void PrintTileYellow() {
        TuiPrinter.tuiPrintTile(Color.YELLOW);
    }

    @Test
    void PrintTileBlue() {
        TuiPrinter.tuiPrintTile(Color.BLUE);
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
        TuiPrinter.tuiPrintShelfie(shelfie);
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
        TuiPrinter.tuiPrintShelfie(shelfie);
    }

    @Test
    void tuiPrintEmptyBoard() {

        TuiPrinter.tuiPrintBoard(new Board(4));
    }

    @Test
    void tuiPrintBoardRandom() {
        TuiPrinter.tuiPrintBoard(Game.refillBoard(new Board(4), BAG));
    }
}