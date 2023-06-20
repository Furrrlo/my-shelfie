package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.PersonalGoalView;
import it.polimi.ingsw.model.ShelfieView;

/**
 * Printer for {@link PersonalGoalView}.
 * <p>
 * Prints a shelfie representing the given personal goal
 * 
 * @see TuiShelfiePrinter
 */
public class TuiPersonalGoalPrinter implements TuiPrinter {

    private final PersonalGoalView personalGoal;

    public TuiPersonalGoalPrinter(PersonalGoalView personalGoal) {
        this.personalGoal = personalGoal;
    }

    @Override
    public void print(TuiPrintStream out) {
        TuiShelfiePrinter.printShelfieMatrix(out, personalGoal::get, (row, col) -> false);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(ShelfieView.ROWS + 1, ShelfieView.COLUMNS * 3);
    }

}
