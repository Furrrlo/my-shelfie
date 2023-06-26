package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#CROSS}
 */
@Immutable
public class CrossCommonGoalChecker implements CommonGoalChecker {

    public boolean checkForCross(Shelfie shelfie, int[][] checked) {
        int count = 0;
        for (int r = 0; r < ROWS - 2; r++) {
            for (int c = 0; c < COLUMNS - 2; c++) {
                Tile tile = shelfie.tile(r, c).get();
                if (tile != null &&
                        tile.equals(shelfie.tile(r + 2, c).get()) &&
                        tile.equals(shelfie.tile(r, c + 2).get()) &&
                        tile.equals(shelfie.tile(r + 1, c + 1).get()) &&
                        tile.equals(shelfie.tile(r + 2, c + 2).get())) {
                    count++;
                    checked[r][c] = count;
                    checked[r + 2][c] = count;
                    checked[r][c + 2] = count;
                    checked[r + 2][c + 2] = count;
                    checked[r + 1][c + 1] = count;
                }
            }
        }
        return count > 0;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int[][] checked = new int[ROWS][COLUMNS];
        return checkForCross(shelfie, checked);
    }
}
