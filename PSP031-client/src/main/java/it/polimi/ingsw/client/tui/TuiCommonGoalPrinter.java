package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.CommonGoalView;
import it.polimi.ingsw.model.ShelfieView;
import it.polimi.ingsw.model.Type;

/**
 * Printer for {@link CommonGoalView}.
 * <p>
 * Prints a description of the given common goal card and an example shelfie that achieve the goal,
 * according to its {@link Type}
 *
 * @see TuiShelfiePrinter
 */
class TuiCommonGoalPrinter implements TuiPrinter {
    private final Type type;

    public TuiCommonGoalPrinter(Type type) {
        this.type = type;
    }

    @Override
    public void print(TuiPrintStream out) {
        TuiShelfiePrinter.printShelfieMatrixByColor(out, type.getExample()::get, (row, col) -> true);
    }

    @Override
    public TuiSize getSize() {
        return new TuiSize(ShelfieView.ROWS + 1, ShelfieView.COLUMNS * 3);
    }
}
