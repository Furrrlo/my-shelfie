package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

@Immutable
public class SixCouplesCommonGoalChecker implements CommonGoalChecker {

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int[][] checked = new int[ROWS][COLUMNS];
        return numCouples(shelfie, checked) >= 6;
    }

    /**
     * Returns number of couples in given shelfie and marks them with progressive number representing the
     * order in which they have been identified by the program
     **/
    public int numCouples(Shelfie shelfie, int[][] checked) {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (c < COLUMNS - 1 && shelfie.tile(r, c).get() != null
                        && Objects.equals(shelfie.tile(r, c + 1).get(), shelfie.tile(r, c).get()) &&
                        checked[r][c] == 0 && checked[r][c + 1] == 0) {
                    count++;
                    checked[r][c] = count;
                    checked[r][c + 1] = count;
                }
                if (r < ROWS - 1 && shelfie.tile(r, c).get() != null
                        && Objects.equals(shelfie.tile(r + 1, c).get(), shelfie.tile(r, c).get()) &&
                        checked[r][c] == 0 && checked[r + 1][c] == 0) {
                    count++;
                    checked[r][c] = count;
                    checked[r + 1][c] = count;
                }
            }
        }
        return count;
    }
}
