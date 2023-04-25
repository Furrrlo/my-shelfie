package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}