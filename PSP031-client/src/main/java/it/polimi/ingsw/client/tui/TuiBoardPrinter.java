package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.BoardView;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

class TuiBoardPrinter implements TuiPrinter {

    private final BoardView board;

    public TuiBoardPrinter(BoardView board) {
        this.board = board;
    }

    @Override
    public void print(TuiPrintStream out) {
        for (int row = 0; row < board.getRows(); row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                msg.append("  ");
                for (int i = 0; i < board.getCols(); i++)
                    msg.append(' ').append((char) ('A' + i)).append(' ');
                msg.append('\n');
            }

            for (int col = 0; col < board.getCols(); col++) {
                if (col == 0)
                    msg.append(row + 1).append(' ');

                var tile = board.isValidTile(row, col) ? board.tile(row, col).get() : null;
                if (tile == null) {
                    msg.append(ConsoleColors.BLACK_BACKGROUND_BRIGHT).append(pxl).append(ConsoleColors.RESET);
                    continue;
                }

                String consoleColor = TuiColorConverter.color(tile.getColor(), board.hasFreeSide(row, col));
                msg.append(consoleColor).append(pxl).append(ConsoleColors.RESET);
            }
            out.println(msg);
        }
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(BoardView.BOARD_ROWS + 1, BoardView.BOARD_COLUMNS * 3 + 2);
    }
}
