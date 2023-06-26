package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#TWO_SQUARES}
 */
@Immutable
public class TwoSquaresCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns the number of existing squares in a given shelfie, and marks the existing ones with
     * progressive numbers according to the order they have been found
     **/
    public int numSquares(Shelfie shelfie, int[][] checked) {
        int count = 0;
        for (int r = 0; r < ROWS - 1; r++) {
            for (int c = 0; c < COLUMNS - 1; c++) {
                if (checked[r][c] == 0 && /* checked[r + 1][c] == 0 && */ checked[r][c + 1] == 0 /*
                                                                                                  * && checked[r + 1][c + 1] ==
                                                                                                  * 0
                                                                                                  */) {
                    if (shelfie.tile(r, c).get() != null &&
                            Objects.equals(shelfie.tile(r + 1, c).get(), shelfie.tile(r, c).get()) &&
                            Objects.equals(shelfie.tile(r, c + 1).get(), shelfie.tile(r, c).get()) &&
                            Objects.equals(shelfie.tile(r + 1, c + 1).get(), shelfie.tile(r, c).get())) {
                        count++;
                        checked[r][c] = count;
                        checked[r + 1][c] = count;
                        checked[r][c + 1] = count;
                        checked[r + 1][c + 1] = count;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int[][] checked = new int[ROWS][COLUMNS];
        return numSquares(shelfie, checked) >= 2;
    }
}
