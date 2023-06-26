package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#TRIANGLE}
 */
@Immutable
public class TriangleCommonGoalChecker implements CommonGoalChecker {
    /**
     * Returns true if in the specified shelfie is present a triangle, and marks it if found ( it looks for
     * triangle both going from left to right, and right to left )
     */
    public boolean checkForTriangle(Shelfie shelfie, int[][] checked) {
        int count = 0;
        int r = 1;
        if (shelfie.tile(r, 0).get() != null &&
                shelfie.tile(r + 1, 1).get() != null &&
                shelfie.tile(r + 2, 2).get() != null &&
                shelfie.tile(r + 3, 3).get() != null &&
                shelfie.tile(r + 4, 4).get() != null) {
            count++;
            checked[r][0] = count;
            checked[r + 1][1] = count;
            checked[r + 2][2] = count;
            checked[r + 3][3] = count;
            checked[r + 4][4] = count;
        }

        if (shelfie.tile(r, 4).get() != null &&
                shelfie.tile(r + 1, 3).get() != null &&
                shelfie.tile(r + 2, 2).get() != null &&
                shelfie.tile(r + 3, 1).get() != null &&
                shelfie.tile(r + 4, 0).get() != null) {
            count++;
            checked[r][4] = count;
            checked[r + 1][3] = count;
            checked[r + 2][2] = count;
            checked[r + 3][1] = count;
            checked[r + 4][0] = count;

        }
        return count > 0;

    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int[][] checked = new int[ROWS][COLUMNS];
        return checkForTriangle(shelfie, checked);
    }
}
