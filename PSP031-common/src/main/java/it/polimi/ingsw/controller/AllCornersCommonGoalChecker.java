package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#ALL_CORNERS}
 */
@Immutable
public class AllCornersCommonGoalChecker implements CommonGoalChecker {

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        return shelfie.tile(0, 0).get() != null &&
                Objects.equals(shelfie.tile(0, 0).get(), shelfie.tile(0, COLUMNS - 1).get()) &&
                Objects.requireNonNull(shelfie.tile(0, COLUMNS - 1).get()).equals(shelfie.tile(ROWS - 1, 0).get()) &&
                Objects.requireNonNull(shelfie.tile(ROWS - 1, 0).get()).equals(shelfie.tile(ROWS - 1, COLUMNS - 1).get());
    }
}
