package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;
import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

class TuiGameScene implements TuiScene {

    private final GameView game;

    public TuiGameScene(GameView game) {
        this.game = game;
    }

    @Override
    public void render(TuiPrintStream out) {
        var terminalSize = out.getTerminalSize();
        var detailedNeededSize = getDetailedOuterRect(out).size();
        var normalNeededSize = new TuiSize(40, 156);

        if (terminalSize.rows() >= detailedNeededSize.rows() && terminalSize.cols() >= detailedNeededSize.cols()) {
            printDetailed(out);
        } else if (terminalSize.rows() >= normalNeededSize.rows() && terminalSize.cols() >= normalNeededSize.cols()) {
            printNormal(out, detailedNeededSize);
        } else {
            out.printAligned(new TuiStringPrinter("Terminal is not big enough to display the game, " +
                    "terminal size " + terminalSize.cols() + "x" + terminalSize.rows() + ", " +
                    "zoom out to " + normalNeededSize.cols() + "x" + normalNeededSize.rows(), terminalSize.cols()),
                    new TuiRect(0, 0, terminalSize),
                    TuiHAlignment.CENTER,
                    TuiVAlignment.TOP);
            out.cursor(2, 0);
        }
    }

    private void printNormal(TuiPrintStream out, TuiSize detailedSize) {
        var terminalSize = out.getTerminalSize();
        // Print terminal size in the top-left corner
        out.printAligned(new TuiStringPrinter("terminal size " + terminalSize.cols() + "x" + terminalSize.rows() + ", " +
                "zoom out to " + detailedSize.cols() + "x" + detailedSize.rows() + " for a detailed view", terminalSize.cols()),
                new TuiRect(0, 0, terminalSize),
                TuiHAlignment.CENTER,
                TuiVAlignment.TOP);

        //Draw personal goal
        var personalGoalRect = out.getAlignedRect(new TuiSize(ROWS + 6, 32),
                new TuiRect(0, 0, terminalSize),
                TuiHAlignment.LEFT,
                TuiVAlignment.TOP);

        out.cursor(personalGoalRect.row(), personalGoalRect.col());
        out.println("Personal goal");
        out.printBox(
                TuiRect.fromCoords(
                        personalGoalRect.row(), personalGoalRect.col(),
                        personalGoalRect.lastRow() + 1, personalGoalRect.lastCol() + 1),
                TuiPrintStream.BOX_RIGHT | TuiPrintStream.BOX_BOTTOM);

        var personalGoalShelfie = out.printAligned(
                new TuiPersonalGoalPrinter(game.getPersonalGoal()),
                TuiRect.fromCoords(personalGoalRect.row() + 2,
                        personalGoalRect.col(),
                        personalGoalRect.lastRow() - 3,
                        personalGoalRect.lastCol()),
                TuiHAlignment.CENTER, TuiVAlignment.CENTER);

        var messageSize = new TuiSize(2, personalGoalRect.lastCol());
        if (game.getPersonalGoal().achievedPersonalGoal(game.thePlayer().getShelfie()))
            out.printAligned(new TuiStringPrinter(
                    ConsoleColors.GREEN_BOLD_BRIGHT + "You achieved your personal goal!" + ConsoleColors.RESET, messageSize),
                    new TuiRect(personalGoalShelfie.lastRow() + 2,
                            personalGoalRect.col(),
                            messageSize),
                    TuiHAlignment.CENTER,
                    TuiVAlignment.CENTER);
        else
            out.printAligned(new TuiStringPrinter("You matched "
                    + game.thePlayer().getShelfie().numTilesOverlappingWithPersonalGoal(game.getPersonalGoal())
                    + " tiles with your personal goal", messageSize),
                    new TuiRect(personalGoalShelfie.lastRow() + 2,
                            personalGoalRect.col(),
                            messageSize),
                    TuiHAlignment.CENTER,
                    TuiVAlignment.CENTER);

        // Draw board
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

        // Draw CommonGoal 1
        var commonGoalCols = Math.min(terminalSize.cols() - rightPlayerRect.lastCol() - 3, 50);
        var commonGoalRect = out.getAlignedRect(
                new TuiSize(ROWS + 9, commonGoalCols),
                new TuiRect(0, 0, terminalSize),
                TuiHAlignment.RIGHT, TuiVAlignment.TOP);

        out.printAligned(
                new TuiStringPrinter("Common goal : " + game.getCommonGoals().get(0).getType().name().replace('_', ' '),
                        commonGoalCols),
                commonGoalRect,
                TuiHAlignment.CENTER,
                TuiVAlignment.TOP);

        out.printBox(
                TuiRect.fromCoords(
                        commonGoalRect.row(), commonGoalRect.col() - 1,
                        commonGoalRect.lastRow() + 1, commonGoalRect.lastCol()),
                TuiPrintStream.BOX_LEFT | TuiPrintStream.BOX_BOTTOM);

        var commonGoal = out.printAligned(new TuiCommonGoalPrinter(game.getCommonGoals().get(0).getType()),
                TuiRect.fromCoords(commonGoalRect.row() + 2, commonGoalRect.col(),
                        commonGoalRect.lastRow() - 6, commonGoalRect.lastCol()),
                TuiHAlignment.CENTER, TuiVAlignment.CENTER);

        var commonGoalDesc = new TuiSize(5, commonGoalCols);
        out.printAligned(new TuiStringPrinter(
                game.getCommonGoals().get(0).getType().getDescription(), commonGoalDesc),
                new TuiRect(commonGoal.lastRow() + 2,
                        commonGoalRect.col(),
                        commonGoalDesc),
                TuiHAlignment.CENTER,
                TuiVAlignment.CENTER);

        // Draw CommonGoal 2
        var commonGoalRect2 = out.getAlignedRect(new TuiSize(ROWS + 9, commonGoalCols),
                new TuiRect(0, 0, terminalSize),
                TuiHAlignment.RIGHT, TuiVAlignment.BOTTOM);

        out.printAligned(
                new TuiStringPrinter("Common goal : " + game.getCommonGoals().get(1).getType().name().replace('_', ' '),
                        commonGoalCols),
                commonGoalRect2,
                TuiHAlignment.CENTER,
                TuiVAlignment.TOP);

        out.printBox(
                TuiRect.fromCoords(
                        commonGoalRect2.row() - 1, commonGoalRect2.col() - 1,
                        commonGoalRect2.lastRow(), commonGoalRect2.lastCol()),
                TuiPrintStream.BOX_LEFT | TuiPrintStream.BOX_TOP);

        var commonGoal2 = out.printAligned(new TuiCommonGoalPrinter(game.getCommonGoals().get(1).getType()),
                TuiRect.fromCoords(commonGoalRect2.row() + 2, commonGoalRect2.col(),
                        commonGoalRect2.lastRow() - 6, commonGoalRect2.lastCol()),
                TuiHAlignment.CENTER, TuiVAlignment.CENTER);

        out.printAligned(new TuiStringPrinter(
                game.getCommonGoals().get(1).getType().getDescription(), commonGoalDesc),
                new TuiRect(commonGoal2.lastRow() + 2,
                        commonGoalRect2.col(),
                        commonGoalDesc),
                TuiHAlignment.CENTER,
                TuiVAlignment.CENTER);

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

    private TuiRect getDetailedOuterRect(TuiPrintStream out) {
        final var shelfiePrinterSize = TuiDetailedShelfiePrinter.EMPTY.getSize();
        final var boardPrinterSize = TuiDetailedBoardPrinter.EMPTY.getSize();
        return out.getAlignedRect(
                new TuiSize(
                        Math.max(boardPrinterSize.rows(), shelfiePrinterSize.rows() * 2),
                        boardPrinterSize.cols() + shelfiePrinterSize.cols() * 2),
                new TuiRect(0, 0, out.getTerminalSize()),
                TuiHAlignment.CENTER, TuiVAlignment.CENTER);
    }

    private void printDetailed(TuiPrintStream out) {
        var outerRect = getDetailedOuterRect(out);

        var thePlayerPrinter = new TuiDetailedShelfiePrinter(game.thePlayer().getShelfie());
        var thePlayerRect = out.printAligned(
                thePlayerPrinter,
                outerRect,
                TuiHAlignment.LEFT, TuiVAlignment.TOP);

        var otherPlayers = game.getPlayers().stream()
                .filter(p -> !p.equals(game.thePlayer()))
                .toList();

        var lowerRightPlayer = !otherPlayers.isEmpty() ? otherPlayers.get(0) : null;
        var lowerRightPlayerRect = out.printAligned(
                lowerRightPlayer != null
                        ? new TuiDetailedShelfiePrinter(lowerRightPlayer.getShelfie())
                        : TuiDetailedShelfiePrinter.EMPTY,
                outerRect,
                TuiHAlignment.RIGHT, TuiVAlignment.BOTTOM);

        var lowerLeftPlayer = otherPlayers.size() >= 2 ? otherPlayers.get(1) : null;
        out.printAligned(
                lowerLeftPlayer != null
                        ? new TuiDetailedShelfiePrinter(lowerLeftPlayer.getShelfie())
                        : TuiDetailedShelfiePrinter.EMPTY,
                outerRect,
                TuiHAlignment.LEFT, TuiVAlignment.BOTTOM);

        var upperRightPlayer = otherPlayers.size() >= 3 ? otherPlayers.get(2) : null;
        out.printAligned(
                upperRightPlayer != null
                        ? new TuiDetailedShelfiePrinter(upperRightPlayer.getShelfie())
                        : TuiDetailedShelfiePrinter.EMPTY,
                outerRect,
                TuiHAlignment.RIGHT, TuiVAlignment.TOP);

        var boardPrinter = new TuiDetailedBoardPrinter(game.getBoard(),
                List.of(game.getCommonGoals().get(0).getType(), game.getCommonGoals().get(1).getType()));
        out.printAligned(
                boardPrinter,
                TuiRect.fromCoords(
                        thePlayerRect.row(), thePlayerRect.lastCol() + 1,
                        lowerRightPlayerRect.lastRow(), lowerRightPlayerRect.col() - 1),
                TuiHAlignment.LEFT, TuiVAlignment.CENTER);

        out.cursor(0, 0);
    }

    //TODO: this method can be removed (?)
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
                msg.append(pxl).append(ConsoleColors.RESET);
            }
            out.println(msg);
        }
    }

    //TODO: this method can be removed (?)
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
                    String consoleColor = TuiColorConverter.color(color, false);
                    msg.append(consoleColor).append(pxl).append(ConsoleColors.RESET);
                } else {
                    String consoleColor = TuiColorConverter.color(color, true);
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
