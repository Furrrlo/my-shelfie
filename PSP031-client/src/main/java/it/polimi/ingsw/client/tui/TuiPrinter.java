package it.polimi.ingsw.client.tui;

/**
 * Base interface for all console objects printers
 */
interface TuiPrinter {

    /**
     * Print this object on the given {@link TuiPrintStream}
     * 
     * @param out output stream
     */
    void print(TuiPrintStream out);

    /**
     * @return the printed size
     */
    TuiSize getSize();
}
