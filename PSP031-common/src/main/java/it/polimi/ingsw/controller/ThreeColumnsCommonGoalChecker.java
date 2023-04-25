package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

@Immutable
public class ThreeColumnsCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns the number of different colors present in a given column c of a shelfie, and if the number of
     * colors is less than 4, it marks the columns with a progressive number according to the order they have
     * been found
     */
    public int numColorsForColumn(Shelfie shelfie, int c, int[][] checked, int marker) {
        List<Color> colors = new ArrayList<>();
        boolean fullColumn = true;

        for (int r = 0; r < ROWS && fullColumn; r++) {
            if (shelfie.tile(r, c).get() != null
                    && !colors.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                colors.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
            if (shelfie.tile(r, c).get() == null)
                fullColumn = false;
        }
        if (colors.size() <= 3 && fullColumn)
            for (int r = 0; r < ROWS; r++) {
                checked[r][c] = marker;
            }
        return fullColumn ? colors.size() : COLUMNS;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int count = 0;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int c = 0; c < COLUMNS; c++) {
            if (numColorsForColumn(shelfie, c, checked, count + 1) <= 3)
                count++;
        }
        return (count >= 3);
    }
}
