package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

public class TriangleCommonGoalChecker implements CommonGoalChecker {

    public boolean checkForTriangle(Shelfie shelfie, int[][] checked) {
        int count = 0;
        for (int r = 0; r < 2; r++) {
            if (shelfie.tile(r, 0).get() != null && shelfie.tile(r + 1, 0).get() == null &&
                    shelfie.tile(r + 1, 1).get() != null && shelfie.tile(r + 2, 1).get() == null &&
                    shelfie.tile(r + 2, 2).get() != null && shelfie.tile(r + 3, 2).get() == null &&
                    shelfie.tile(r + 3, 3).get() != null && shelfie.tile(r + 4, 3).get() == null &&
                    shelfie.tile(r + 4, 4).get() != null) {
                count++;
                checked[r][0] = count;
                checked[r + 1][1] = count;
                checked[r + 2][2] = count;
                checked[r + 3][3] = count;
                checked[r + 4][4] = count;
            }
        }
        for (int r = 0; r < 2; r++) {
            if (shelfie.tile(r, 4).get() != null && shelfie.tile(r + 1, 4).get() == null &&
                    shelfie.tile(r + 1, 3).get() != null && shelfie.tile(r + 2, 3).get() == null &&
                    shelfie.tile(r + 2, 2).get() != null && shelfie.tile(r + 3, 2).get() == null &&
                    shelfie.tile(r + 3, 1).get() != null && shelfie.tile(r + 4, 1).get() == null &&
                    shelfie.tile(r + 4, 0).get() != null) {
                count++;
                checked[r][4] = count;
                checked[r + 1][3] = count;
                checked[r + 2][2] = count;
                checked[r + 3][1] = count;
                checked[r + 4][0] = count;
            }
        }
        return count > 0;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int[][] checked = new int[ROWS][COLUMNS];
        return checkForTriangle(shelfie, checked);
    }
}
