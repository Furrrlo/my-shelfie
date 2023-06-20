package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.BoardView;
import it.polimi.ingsw.model.Type;

import java.io.PrintStream;
import java.util.List;

import static it.polimi.ingsw.client.tui.TuiDetailedNumberPrinter.PXL_COLS_FOR_NUMBERS;
import static it.polimi.ingsw.client.tui.TuiDetailedNumberPrinter.PXL_ROWS_FOR_NUMBERS;
import static it.polimi.ingsw.client.tui.TuiDetailedTilePrinter.PXL_FOR_SPRITE;
import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;
import static it.polimi.ingsw.model.BoardView.BOARD_COLUMNS;
import static it.polimi.ingsw.model.BoardView.BOARD_ROWS;

/**
 * Printer for {@link BoardView}.
 * <p>
 * Prints a detailed drawing of the board
 * 
 * @see TuiDetailedTilePrinter
 */
class TuiDetailedBoardPrinter implements TuiPrinter {

    public static final TuiDetailedBoardPrinter EMPTY = new TuiDetailedBoardPrinter(new Board(2),
            List.of(Type.FOUR_QUADRIPLETS, Type.SIX_COUPLES)) {
        @Override
        public void print(TuiPrintStream out) {
        }
    };

    private final BoardView board;
    private final List<Type> commonGoalTypes;

    public TuiDetailedBoardPrinter(BoardView board, List<Type> commonGoalTypes) {
        this.board = board;
        this.commonGoalTypes = commonGoalTypes;
    }

    @Override
    public void print(TuiPrintStream out) {
        printBoardSeparatingLine(out);
        printBoardNumbers(out);

        for (int row = 0; row < BOARD_ROWS; row++) {
            var numberPrinter = TuiDetailedNumberPrinter.of(row + 1, true);
            numberPrinter.print(out);
            out.moveCursorUp(numberPrinter.getSize().rows());

            int colOffset = numberPrinter.getSize().cols();
            try (var ignored = out.saveCursorPos();
                 var ignored1 = out.translateCursorToCol(colOffset)) {

                for (int i = 0; i < PXL_FOR_SPRITE; i++)
                    out.println(ConsoleColors.BLUE_BACKGROUND_BRIGHT + pxl + ConsoleColors.RESET);
                colOffset += pxl.length();
            }

            for (int col = 0; col < BOARD_COLUMNS; col++) {
                try (var ignored = out.saveCursorPos();
                     var ignored1 = out.translateCursorToCol(colOffset)) {

                    if (board.isValidTile(row, col)) {
                        var tilePrinter = TuiDetailedTilePrinter.of(board.tile(row, col).get());
                        tilePrinter.print(out);
                        colOffset += tilePrinter.getSize().cols();
                    } else {
                        if (row == BOARD_ROWS - 1 && col == BOARD_COLUMNS - 2) {
                            var commonGoalPrinter = TuiDetailedCommonGoalPrinter.of(commonGoalTypes.get(0));
                            commonGoalPrinter.print(out);
                            colOffset += commonGoalPrinter.getSize().cols();
                        } else if (row == BOARD_ROWS - 1 && col == BOARD_COLUMNS - 1) {
                            var commonGoalPrinter = TuiDetailedCommonGoalPrinter.of(commonGoalTypes.get(1));
                            commonGoalPrinter.print(out);
                            colOffset += commonGoalPrinter.getSize().cols();
                        } else
                            colOffset += printInvalidTile(out);
                    }
                }

                try (var ignored = out.saveCursorPos();
                     var ignored1 = out.translateCursorToCol(colOffset)) {

                    for (int i = 0; i < PXL_FOR_SPRITE; i++)
                        out.println(ConsoleColors.BLUE_BACKGROUND_BRIGHT + pxl + ConsoleColors.RESET);
                    colOffset += pxl.length();
                }
            }

            out.moveCursorDown(numberPrinter.getSize().rows());
            printBoardSeparatingLine(out);
        }
    }

    private static void printBoardSeparatingLine(PrintStream out) {
        out.println(ConsoleColors.BLUE_BACKGROUND + pxl.repeat(PXL_ROWS_FOR_NUMBERS) +
                ConsoleColors.BLUE_BACKGROUND_BRIGHT + pxl.repeat((PXL_COLS_FOR_NUMBERS + 1) * BOARD_COLUMNS + 1) +
                ConsoleColors.RESET);
    }

    private static void printBoardNumbers(TuiPrintStream out) {
        for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++)
            out.println(ConsoleColors.BLUE_BACKGROUND + pxl.repeat((PXL_ROWS_FOR_NUMBERS + 1)) + ConsoleColors.RESET);
        out.moveCursorUp(PXL_ROWS_FOR_NUMBERS);

        int colOffset = (PXL_ROWS_FOR_NUMBERS + 1) * pxl.length();
        for (int col = 0; col < BOARD_COLUMNS; col++) {
            try (var ignored = out.saveCursorPos();
                 var ignored1 = out.translateCursorToCol(colOffset)) {

                var numberPrinter = TuiDetailedNumberPrinter.of(col + 1);
                numberPrinter.print(out);
                colOffset += numberPrinter.getSize().cols();
            }

            try (var ignored = out.saveCursorPos();
                 var ignored1 = out.translateCursorToCol(colOffset)) {

                for (int i = 0; i < PXL_ROWS_FOR_NUMBERS; i++)
                    out.println(ConsoleColors.BLUE_BACKGROUND + pxl + ConsoleColors.RESET);
                colOffset += pxl.length();
            }
        }

        out.moveCursorDown(PXL_ROWS_FOR_NUMBERS);
    }

    private static int printInvalidTile(PrintStream out) {
        for (int i = 0; i < PXL_FOR_SPRITE; i++)
            out.println(ConsoleColors.BLUE_BACKGROUND + pxl.repeat(PXL_FOR_SPRITE) + ConsoleColors.RESET);
        return PXL_FOR_SPRITE * pxl.length();
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(
                1 + PXL_ROWS_FOR_NUMBERS + (PXL_FOR_SPRITE + 1) * BOARD_ROWS,
                ((PXL_ROWS_FOR_NUMBERS + 1) + (PXL_COLS_FOR_NUMBERS + 1) * BOARD_COLUMNS) * pxl.length());
    }
}
