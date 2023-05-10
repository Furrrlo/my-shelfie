package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.ShelfieView;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

class TuiShelfiePrinter implements TuiPrinter {

    public static TuiShelfiePrinter EMPTY = new TuiShelfiePrinter(new Shelfie()) {
        @Override
        public void print(TuiPrintStream out) {
        }
    };

    private final ShelfieView shelfie;

    public TuiShelfiePrinter(ShelfieView shelfie) {
        this.shelfie = shelfie;
    }

    @Override
    public void print(TuiPrintStream out) {
        printShelfieMatrix(out, (row, col) -> shelfie.tile(row, col).get(), (row, col) -> false);
    }

    static void printShelfieMatrix(TuiPrintStream out,
                                   BiFunction<Integer, Integer, @Nullable Tile> tiles,
                                   BiPredicate<Integer, Integer> highlight) {
        printShelfieMatrixByColor(out, (row, col) -> {
            Tile tile = tiles.apply(row, col);
            if (tile == null)
                return null;
            else
                return tile.getColor();
        }, highlight);
    }

    static void printShelfieMatrixByColor(TuiPrintStream out,
                                          BiFunction<Integer, Integer, @Nullable Color> tiles,
                                          BiPredicate<Integer, Integer> highlight) {
        for (int row = 0; row < ShelfieView.ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                for (int i = 1; i <= ShelfieView.COLUMNS; i++)
                    msg.append(' ').append(i).append(' ');
                msg.append('\n');
            }

            for (int col = 0; col < ShelfieView.COLUMNS; col++) {
                Color tile = tiles.apply(row, col);
                if (tile == null) {
                    msg.append("│ │");
                    continue;
                }

                String consoleColor = TuiColorConverter.color(tile, highlight.test(row, col));
                msg.append(consoleColor).append(pxl).append(ConsoleColors.RESET);
            }
            out.println(msg);
        }
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(ShelfieView.ROWS + 1, ShelfieView.COLUMNS * 3);
    }
}
