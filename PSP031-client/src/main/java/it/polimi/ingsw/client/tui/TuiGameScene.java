package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

class TuiGameScene implements Consumer<TuiPrintStream> {

    private final GameView game;

    public TuiGameScene(GameView game) {
        this.game = game;
    }

    @Override
    public void accept(TuiPrintStream out) {
        // TODO: game renderer, possibly in a different class
        printBoard(out, game.getBoard());
        out.println();

        int col = 0;
        for (PlayerView player : game.getPlayers()) {
            try (NoExceptionAutoCloseable ignored = out.saveCursorPos();
                 NoExceptionAutoCloseable ignored1 = out.translateCursorToCol(col)) {

                final String nickColor;
                if (!player.connected().get())
                    nickColor = ConsoleColors.RED;
                else if (player.isCurrentTurn().get())
                    nickColor = ConsoleColors.YELLOW;
                else
                    nickColor = ConsoleColors.GREEN;
                out.print(nickColor);
                out.print(player.isCurrentTurn().get() ? "> " : "· ");
                out.print(player.getNick());

                if (player.isStartingPlayer()) {
                    out.print(ConsoleColors.PURPLE);
                    out.print(" C");
                    out.print(nickColor);
                }

                if (player.isFirstFinisher().get()) {
                    out.print(ConsoleColors.BLUE);
                    out.print(" *");
                    out.print(nickColor);
                }

                out.print(": " + player.score().get());
                out.println(ConsoleColors.RESET);

                printShelfie(out, player.getShelfie());
                col += 30;
            }
        }

        out.moveCursorDown(10);
    }

    /** prints colored shelfie */
    public static void printShelfie(TuiPrintStream out, ShelfieView shelfie) {
        printShelfieMatrix(out, (row, col) -> shelfie.tile(row, col).get());
    }

    public static void printShelfieMatrix(TuiPrintStream out, BiFunction<Integer, Integer, @Nullable Tile> tiles) {
        for (int row = 0; row < Shelfie.ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0)
                msg.append("   1  2  3  4  5 \n");
            for (int col = 0; col < Shelfie.COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(" ");

                Tile tile = tiles.apply(row, col);
                if (tile == null) {
                    msg.append("| |");
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

    /** prints the shelfie corresponding to the personal goal whose calling the method */
    public static void printPersonalGoal(TuiPrintStream out, PersonalGoalView personalGoal) {
        out.println("PERSONAL GOAL : " + personalGoal.getIndex());
        printShelfieMatrix(out, personalGoal::get);
    }

    /**
     * prints the Shelfie passed as parameter marking with progressive number the tiles that corresponds to the personal goal
     */
    public static void printPersonalGoalOnShelfie(TuiPrintStream out, PersonalGoalView personalGoal, ShelfieView shelfie) {
        int count = 0;
        int[][] checked = new int[Shelfie.ROWS][Shelfie.COLUMNS];
        for (int r = 0; r < Shelfie.ROWS; r++) {
            for (int c = 0; c < Shelfie.COLUMNS; c++) {
                if (personalGoal.get(r, c) != null) {
                    count++;
                    checked[r][c] = count;
                }
            }
        }
        printCommonGoal(out, shelfie, checked, "Congratulation you achieved PERSONAL GOAL");
    }

    public static void printCommonGoal(TuiPrintStream out, ShelfieView shelfie, int[][] checked, String s) {
        out.println(s);
        for (int row = 0; row < ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                msg.append("   1  2  3  4  5 \n");
            }
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(" ");

                Tile tile = shelfie.tile(row, col).get();
                if (tile == null) {
                    msg.append("| |");
                    continue;
                }

                Color color = tile.getColor();
                if (checked[row][col] < 1) {
                    String consoleColor = switch (color) {
                        case BLUE -> ConsoleColors.BLUE_BACKGROUND;
                        case GREEN -> ConsoleColors.GREEN_BACKGROUND;
                        case YELLOW -> ConsoleColors.ORANGE_BACKGROUND;
                        case PINK -> ConsoleColors.PURPLE_BACKGROUND;
                        case WHITE -> ConsoleColors.YELLOW_BACKGROUND;
                        case LIGHTBLUE -> ConsoleColors.CYAN_BACKGROUND;
                    };
                    msg.append(consoleColor).append("   ").append(ConsoleColors.RESET);
                } else {
                    String consoleColor = switch (color) {
                        case BLUE -> ConsoleColors.BLUE_BACKGROUND_BRIGHT;
                        case GREEN -> ConsoleColors.GREEN_BACKGROUND_BRIGHT;
                        case YELLOW -> ConsoleColors.ORANGE_BACKGROUND_BRIGHT;
                        case PINK -> ConsoleColors.PURPLE_BACKGROUND_BRIGHT;
                        case WHITE -> ConsoleColors.YELLOW_BACKGROUND_BRIGHT;
                        case LIGHTBLUE -> ConsoleColors.CYAN_BACKGROUND_BRIGHT;
                    };
                    msg.append(consoleColor)
                            .append(ConsoleColors.BLACK_BOLD)
                            .append(" ").append(checked[row][col]).append(" ")
                            .append(ConsoleColors.RESET);
                }
            }
            out.println(msg);
        }
    }

    public static void printBoard(TuiPrintStream out, BoardView board) {
        for (int row = 0; row < BoardView.BOARD_ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0) {
                msg.append(' ');
                for (int i = 1; i <= BoardView.BOARD_COLUMNS; i++)
                    msg.append(' ').append(i).append(' ');
                msg.append('\n');
            }
            for (int col = 0; col < BoardView.BOARD_COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(" ");

                if (board.isValidTile(row, col)) {
                    @Nullable
                    Tile tile = board.tile(row, col).get();
                    if (tile != null) {
                        switch (tile.getColor()) {
                            case BLUE -> msg.append(ConsoleColors.CYAN).append(ConsoleColors.BLUE_BACKGROUND_BRIGHT);
                            case GREEN -> msg.append(ConsoleColors.GREEN).append(ConsoleColors.GREEN_BACKGROUND_BRIGHT);
                            case YELLOW -> msg.append(ConsoleColors.YELLOW_BRIGHT)
                                    .append(ConsoleColors.ORANGE_BACKGROUND_BRIGHT);
                            case PINK -> msg.append(ConsoleColors.PURPLE).append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT);
                            case WHITE -> msg.append(ConsoleColors.ORANGE).append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT);
                            case LIGHTBLUE -> msg.append(ConsoleColors.BLUE).append(ConsoleColors.CYAN_BACKGROUND_BRIGHT);
                        }
                    } else {
                        msg.append(ConsoleColors.BLACK).append(ConsoleColors.BLACK_BACKGROUND_BRIGHT);
                    }
                } else {
                    msg.append(ConsoleColors.BLACK).append(ConsoleColors.BLACK_BACKGROUND_BRIGHT);
                }

                msg.append("   ").append(ConsoleColors.RESET);
            }
            out.println(msg);
        }
    }
}
