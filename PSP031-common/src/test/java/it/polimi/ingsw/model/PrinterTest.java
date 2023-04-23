package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

class PrinterTest {

    @Test
    void printTileGreen1() {
        Tile t = new Tile(Color.GREEN);
        Printer.printTile(t);
    }
}