package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
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
        var terminalSize = out.getTerminalSize();
        var boardRect = out.printAligned(
                new TuiBoardPrinter(game.getBoard()),
                new TuiRect(0, 0, terminalSize),
                TuiHAlignment.CENTER, TuiVAlignment.CENTER);
        boardRect = boardRect.expand(4, 6);

        BiConsumer<PlayerView, TuiRect> printPlayerNick = (player, shelfieRect) -> {
            out.cursor(shelfieRect.row() - 2, shelfieRect.col());

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
            out.print(ConsoleColors.RESET);
        };

        // thePlayer shelfie, always bottom-centered
        var bottomPlayerRect = out.printAligned(
                new TuiShelfiePrinter(game.thePlayer().getShelfie()),
                TuiRect.fromCoords(boardRect.lastRow() + 3, 0, terminalSize.rows(), terminalSize.cols()),
                TuiHAlignment.CENTER, TuiVAlignment.TOP);
        printPlayerNick.accept(game.thePlayer(), bottomPlayerRect);
        bottomPlayerRect = bottomPlayerRect.expand(4, 6);
        out.printBox(
                TuiRect.fromCoords(
                        boardRect.lastRow() + 1, boardRect.col(),
                        bottomPlayerRect.lastRow(), boardRect.lastCol()),
                TuiPrintStream.BOX_LEFT | TuiPrintStream.BOX_RIGHT | TuiPrintStream.BOX_BOTTOM);

        var otherPlayers = game.getPlayers().stream()
                .filter(p -> !p.equals(game.thePlayer()))
                .toList();

        var topPlayer = !otherPlayers.isEmpty() ? otherPlayers.get(0) : null;
        var topPlayerRect = out.printAligned(
                topPlayer != null ? new TuiShelfiePrinter(topPlayer.getShelfie()) : TuiShelfiePrinter.EMPTY,
                TuiRect.fromCoords(2, 0, boardRect.row() - 1, terminalSize.cols()),
                TuiHAlignment.CENTER, TuiVAlignment.BOTTOM);
        if (topPlayer != null)
            printPlayerNick.accept(topPlayer, topPlayerRect);
        topPlayerRect = topPlayerRect.expand(4, 6);
        out.printBox(
                TuiRect.fromCoords(
                        topPlayerRect.row() - 2, boardRect.col(),
                        boardRect.row() - 1, boardRect.lastCol()),
                TuiPrintStream.BOX_LEFT | TuiPrintStream.BOX_RIGHT | TuiPrintStream.BOX_TOP);

        var leftPlayer = otherPlayers.size() >= 2 ? otherPlayers.get(1) : null;
        var leftPlayerRect = out.printAligned(
                leftPlayer != null ? new TuiShelfiePrinter(leftPlayer.getShelfie()) : TuiShelfiePrinter.EMPTY,
                TuiRect.fromCoords(2, 0, terminalSize.rows(), boardRect.col() - 1),
                TuiHAlignment.RIGHT, TuiVAlignment.CENTER);
        if (leftPlayer != null)
            printPlayerNick.accept(leftPlayer, leftPlayerRect);
        leftPlayerRect = leftPlayerRect.expand(4, 6);
        out.printBox(
                TuiRect.fromCoords(
                        boardRect.row(), leftPlayerRect.col(),
                        boardRect.lastRow(), boardRect.col() - 1),
                TuiPrintStream.BOX_LEFT | TuiPrintStream.BOX_BOTTOM | TuiPrintStream.BOX_TOP);

        var rightPlayer = otherPlayers.size() >= 3 ? otherPlayers.get(2) : null;
        var rightPlayerRect = out.printAligned(
                rightPlayer != null ? new TuiShelfiePrinter(rightPlayer.getShelfie()) : TuiShelfiePrinter.EMPTY,
                TuiRect.fromCoords(2, boardRect.lastCol() + 1, terminalSize.rows(), terminalSize.cols()),
                TuiHAlignment.LEFT, TuiVAlignment.CENTER);
        if (rightPlayer != null)
            printPlayerNick.accept(rightPlayer, rightPlayerRect);
        rightPlayerRect = rightPlayerRect.expand(4, 6);
        out.printBox(
                TuiRect.fromCoords(
                        boardRect.row(), boardRect.lastCol() + 1,
                        boardRect.lastRow(), rightPlayerRect.lastCol()),
                TuiPrintStream.BOX_RIGHT | TuiPrintStream.BOX_BOTTOM | TuiPrintStream.BOX_TOP);

        out.cursor(boardRect.row(), boardRect.col());
        out.print("┘");
        out.cursor(boardRect.row(), boardRect.lastCol());
        out.print("└");
        out.cursor(boardRect.lastRow(), boardRect.col());
        out.print("┐");
        out.cursor(boardRect.lastRow(), boardRect.lastCol());
        out.print("┌");

        out.cursor(boardRect.lastRow() + 2, 0);
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
}
