package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

@Immutable
public class DiagonalCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns true if the diagonal in the shelfie built from tile in position r,c is made by the same colored
     * tiles, and it's made by exactly 5 tiles, otherwise returns false
     */
    public boolean checkDiagonal(Shelfie shelfie, int[][] checked, int r, int c) {
        if (r >= ROWS - 4 || !(c == 0 || c == COLUMNS - 1) || shelfie.tile(r, c).get() == null)
            return false;
        if (c == 0) {
            for (int i = 0; i < 4; i++) {
                if (!Objects.equals(shelfie.tile(r, c).get(), shelfie.tile(r + i, c + i).get()))
                    return false;
            }
            for (int i = 0; i <= 4; i++)
                checked[r + i][c + i] = 1;
        } else {
            for (int i = 0; i > -4; i--) {
                if (!Objects.equals(shelfie.tile(r, c).get(), shelfie.tile(r + i, c + i).get()))
                    return false;
            }
            for (int i = 0; i >= -4; i--)
                checked[r + i][c + i] = 1;
        }
        return true;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        boolean achieved = false;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int r = 0; r < ROWS && !achieved; r++) {
            for (int c = 0; c < COLUMNS && !achieved; c++) {
                achieved = checkDiagonal(shelfie, checked, r, c);
            }
        }
        return achieved;
    }
}
