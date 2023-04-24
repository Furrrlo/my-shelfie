package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

public class AllCornersCommonGoalChecker implements CommonGoalChecker {

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        return shelfie.tile(0, 0).get() != null &&
                Objects.equals(shelfie.tile(0, 0).get(), shelfie.tile(0, COLUMNS - 1).get()) &&
                Objects.requireNonNull(shelfie.tile(0, COLUMNS - 1).get()).equals(shelfie.tile(ROWS - 1, 0).get()) &&
                Objects.requireNonNull(shelfie.tile(ROWS - 1, 0).get()).equals(shelfie.tile(ROWS - 1, COLUMNS - 1).get());
    }
}
