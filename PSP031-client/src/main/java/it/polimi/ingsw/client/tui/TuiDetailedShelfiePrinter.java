package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.ShelfieView;

import java.io.PrintStream;

import static it.polimi.ingsw.client.tui.TuiDetailedNumberPrinter.PXL_ROWS_FOR_NUMBERS;
import static it.polimi.ingsw.client.tui.TuiDetailedTilePrinter.PXL_FOR_SPRITE;
import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;
import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

class TuiDetailedShelfiePrinter implements TuiPrinter {

    public static final TuiDetailedShelfiePrinter EMPTY = new TuiDetailedShelfiePrinter(new Shelfie()) {
        @Override
        public void print(TuiPrintStream out) {
        }
    };

    private final ShelfieView shelfie;

    public TuiDetailedShelfiePrinter(ShelfieView shelfie) {
        this.shelfie = shelfie;
    }

    @Override
    public void print(TuiPrintStream out) {
        printShelfieHeader(out);
        for (int row = 0; row < ROWS; row++) {
            printMidShelf1(out);
            printMidShelf2(out);

            // Make sure we have enough space downwards to save/restore cursor pos
            // by printing new lines and moving back manually the cursor,
            // otherwise the scroll is going to invalidate the position itself
            for (int i = 0; i < PXL_FOR_SPRITE; i++)
                out.println(ConsoleColors.WHITE_BACKGROUND_BRIGHT + pxl +
                        ConsoleColors.ORANGE_BACKGROUND + pxl +
                        ConsoleColors.RESET);
            out.moveCursorUp(PXL_FOR_SPRITE);

            int colOffset = 2 * pxl.length(); // Border
            for (int col = 0; col < COLUMNS; col++) {
                try (var ignored = out.saveCursorPos();
                     var ignored1 = out.translateCursorToCol(colOffset)) {

                    var printer = TuiDetailedTilePrinter.of(shelfie.tile(row, col).get());
                    printer.print(out);
                    colOffset += printer.getSize().cols();
                }

                try (var ignored = out.saveCursorPos();
                     var ignored1 = out.translateCursorToCol(colOffset)) {

                    for (int i = 0; i < PXL_FOR_SPRITE; i++)
                        out.println(ConsoleColors.BROWN_DARK_BACKGROUND + pxl +
                                ConsoleColors.ORANGE_BACKGROUND + pxl +
                                ConsoleColors.RESET);
                    colOffset += 2 * pxl.length();
                }
            }

            out.moveCursorDown(PXL_FOR_SPRITE);
        }
        printShelfieBottom(out);
    }

    private static void printShelfieHeader(TuiPrintStream out) {
        printMidShelf1(out);
        printMidShelf3(out);
        printMidShelf1(out);
        printNumberHeader(out);
        printNumbers(out);
        printNumberHeader(out);
        printMidShelf1(out);
        printMidShelf3(out);
    }

    private static void printNumbers(TuiPrintStream out) {
        // Make sure we have enough space downwards to save/restore cursor pos
        // by printing new lines and moving back manually the cursor,
        // otherwise the scroll is going to invalidate the position itself
        for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++)
            out.println(ConsoleColors.WHITE_BACKGROUND_BRIGHT + pxl +
                    ConsoleColors.ORANGE_BACKGROUND + pxl +
                    ConsoleColors.RESET);
        out.moveCursorUp(PXL_ROWS_FOR_NUMBERS);

        int colOffset = 2 * pxl.length(); // Border
        for (int col = 0; col < COLUMNS; col++) {
            try (var ignored = out.saveCursorPos();
                 var ignored1 = out.translateCursorToCol(colOffset)) {

                var numberPrinter = TuiDetailedNumberPrinter.of(col + 1);
                numberPrinter.print(out);
                colOffset += numberPrinter.getSize().cols();
            }

            try (var ignored = out.saveCursorPos();
                 var ignored1 = out.translateCursorToCol(colOffset)) {

                for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++)
                    out.println(ConsoleColors.BROWN_DARK_BACKGROUND + pxl +
                            ConsoleColors.ORANGE_BACKGROUND + pxl +
                            ConsoleColors.RESET);
                colOffset += 2 * pxl.length();
            }
        }

        out.moveCursorDown(PXL_ROWS_FOR_NUMBERS);
    }

    private static void printNumberHeader(PrintStream out) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
        for (int i = 0; i < COLUMNS; i++)
            sb.append(ConsoleColors.ORANGE_BACKGROUND)
                    .append(pxl).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl.repeat(24))
                    .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl);
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        out.println(sb);
    }

    private static void printShelfieBottom(PrintStream out) {
        printMidShelf1(out);
        printMidShelf3(out);
        printMidShelf1(out);
    }

    private static void printMidShelf1(PrintStream out) {
        out.println(ConsoleColors.WHITE_BACKGROUND_BRIGHT + pxl +
                ConsoleColors.ORANGE_BACKGROUND + pxl.repeat(131) +
                ConsoleColors.RESET);
    }

    private static void printMidShelf2(PrintStream out) {
        out.println(ConsoleColors.WHITE_BACKGROUND_BRIGHT + pxl +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(25) +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(25) +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(25) +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(25) +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(25) +
                ConsoleColors.ORANGE_BACKGROUND + pxl +
                ConsoleColors.RESET);
    }

    private static void printMidShelf3(PrintStream out) {
        out.println(ConsoleColors.BROWN_DARK_BACKGROUND + pxl.repeat(131) + ConsoleColors.RESET);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(
                4 + PXL_ROWS_FOR_NUMBERS + 3 + (2 + PXL_FOR_SPRITE) * ROWS + 3,
                (2 + (PXL_FOR_SPRITE + 2) * COLUMNS) * pxl.length());
    }
}
