package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.PersonalGoalView;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;
import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Printer for {@link PersonalGoalView}.
 * <p>
 * Prints a detailed drawing of the given personal goal card
 */
class TuiDetailedPersonalGoalPrinter implements TuiPrinter {

    private static final int PXL_FOR_PERSONAL_GOAL = 12;

    private final PersonalGoalView goal;

    public TuiDetailedPersonalGoalPrinter(PersonalGoalView goal) {
        this.goal = goal;
    }

    @Override
    public void print(TuiPrintStream out) {
        for (int row = 0; row < ROWS; row++) {
            out.println(personalGoalMidShelf1());
            out.println(personalGoalMidShelf2());
            for (int i = 0; i < PXL_FOR_PERSONAL_GOAL; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(ConsoleColors.RESET);
                for (int col = 0; col < COLUMNS; col++) {
                    if (col == 0)
                        sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
                    var tile = goal.get(row, col);
                    if (tile == null)
                        sb.append(ConsoleColors.RESET).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL));
                    else
                        sb.append(personalGoalLine(tile.getColor()));
                    sb.append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl)
                            .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl);
                    sb.append(ConsoleColors.RESET);
                }
                out.println(sb);
            }
        }
        out.println(personalGoalMidShelf1());
    }

    private static StringBuilder personalGoalMidShelf1() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl)
                .append(ConsoleColors.ORANGE_BACKGROUND).append(pxl.repeat(COLUMNS * (PXL_FOR_PERSONAL_GOAL + 2)));
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        return sb;
    }

    private static StringBuilder personalGoalMidShelf2() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.RESET).append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl);
        for (int i = 0; i < COLUMNS; i++)
            sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl)
                    .append(ConsoleColors.BROWN_DARK_BACKGROUND).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL + 1));
        sb.append(ConsoleColors.ORANGE_BACKGROUND).append(pxl).append(ConsoleColors.RESET);
        return sb;
    }

    /** returns the colored String, at given index, of a specified PersonalGoal as StringBuilder */
    private static StringBuilder personalGoalLine(Color color) {
        StringBuilder sb = new StringBuilder();
        return switch (color) {
            case GREEN -> sb.append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case WHITE -> sb.append(ConsoleColors.WHITE_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case BLUE -> sb.append(ConsoleColors.BLUE_BACKGROUND).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case LIGHTBLUE -> sb.append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case YELLOW -> sb.append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
            case PINK -> sb.append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append(pxl.repeat(PXL_FOR_PERSONAL_GOAL))
                    .append(ConsoleColors.RESET);
        };
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(
                (2 + PXL_FOR_PERSONAL_GOAL) * ROWS + 1,
                (2 + (PXL_FOR_PERSONAL_GOAL + 2)) * pxl.length());
    }
}
