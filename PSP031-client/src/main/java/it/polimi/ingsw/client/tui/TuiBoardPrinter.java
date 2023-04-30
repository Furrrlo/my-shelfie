package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.BoardView;

class TuiBoardPrinter implements TuiPrinter2 {

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
                for (int i = 1; i <= board.getCols(); i++)
                    msg.append(' ').append(i).append(' ');
                msg.append('\n');
            }

            for (int col = 0; col < board.getCols(); col++) {
                if (col == 0)
                    msg.append(row + 1).append(' ');

                var tile = board.isValidTile(row, col) ? board.tile(row, col).get() : null;
                if (tile == null) {
                    msg.append(ConsoleColors.BLACK).append(ConsoleColors.BLACK_BACKGROUND_BRIGHT);
                    msg.append("   ").append(ConsoleColors.RESET);
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
        return new TuiSize(BoardView.BOARD_ROWS + 1, BoardView.BOARD_COLUMNS * 3 + 2);
    }
}
