package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

public class FourRowsCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns the number of different colors present in a given row r of a shelfie, excluding null tiles
     */
    public int numColorsForRow(Shelfie shelfie, int[][] checked, int marker, int r) {
        List<Color> count = new ArrayList<>();
        for (int c = 0; c < COLUMNS; c++) {
            //if there is at least a null tile the row doesn't count because it must be full
            if (shelfie.tile(r, c).get() == null)
                return ROWS;
            if (!count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
        }
        if (count.size() < 4)
            Arrays.fill(checked[r], marker);
        return count.size();
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int count = 0;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            if (numColorsForRow(shelfie, checked, count + 1, r) <= 3)
                count++;
        }
        return count >= 4;
    }
}
