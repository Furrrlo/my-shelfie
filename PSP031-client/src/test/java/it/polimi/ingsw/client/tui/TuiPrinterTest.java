package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

class TuiPrinterTest {
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
    void tuiPrintBoardRandom() {
        Board board = new Board(4);
        TuiPrinter.tuiPrintBoard(board);
    }
}