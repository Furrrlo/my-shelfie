package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.ShelfieView;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

class TuiShelfiePrinter implements TuiPrinter2 {

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
        printShelfieMatrix(out, (row, col) -> shelfie.tile(row, col).get());
    }

    static void printShelfieMatrix(TuiPrintStream out, BiFunction<Integer, Integer, @Nullable Tile> tiles) {
        for (int row = 0; row < ShelfieView.ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                msg.append("  ");
                for (int i = 1; i <= ShelfieView.COLUMNS; i++)
                    msg.append(' ').append(i).append(' ');
                msg.append('\n');
            }

            for (int col = 0; col < ShelfieView.COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(' ');

                Tile tile = tiles.apply(row, col);
                if (tile == null) {
                    msg.append("│ │");
                    continue;
                }

                switch (tile.getColor()) {
                    case BLUE -> msg.append(ConsoleColors.CYAN).append(ConsoleColors.BLUE_BACKGROUND_BRIGHT);
                    case GREEN -> msg.append(ConsoleColors.GREEN).append(ConsoleColors.GREEN_BACKGROUND_BRIGHT);
                    case YELLOW -> msg.append(ConsoleColors.YELLOW_BRIGHT).append(ConsoleColors.ORANGE_BACKGROUND_BRIGHT);
                    case PINK -> msg.append(ConsoleColors.PURPLE).append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT);
                    case WHITE -> msg.append(ConsoleColors.ORANGE).append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
                    case LIGHTBLUE -> msg.append(ConsoleColors.BLUE).append(ConsoleColors.CYAN_BACKGROUND_BRIGHT);
                }

                msg.append("   ").append(ConsoleColors.RESET);
            }
            out.println(msg);
        }
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(ShelfieView.ROWS + 1, ShelfieView.COLUMNS * 3 + 2);
    }
}
