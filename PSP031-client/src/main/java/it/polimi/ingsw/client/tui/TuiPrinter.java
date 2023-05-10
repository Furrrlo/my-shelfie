package it.polimi.ingsw.client.tui;

interface TuiPrinter {

    void print(TuiPrintStream out);

    TuiSize getSize();
}
