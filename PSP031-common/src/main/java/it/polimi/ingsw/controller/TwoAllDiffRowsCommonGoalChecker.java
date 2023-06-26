package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#TWO_ALL_DIFF_ROWS}
 */
@Immutable
public class TwoAllDiffRowsCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns the number of different colors present in a given row r of a shelfie, excluding null tiles
     */
    public int numColorsForRow(Shelfie shelfie, int[][] checked, int marker, int r) {
        List<Color> count = new ArrayList<>();
        for (int c = 0; c < COLUMNS; c++) {
            if (shelfie.tile(r, c).get() != null
                    && !count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
        }
        if (count.size() == 5)
            Arrays.fill(checked[r], marker);
        return count.size();
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int count = 0;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            if (numColorsForRow(shelfie, checked, count + 1, r) == COLUMNS)
                count++;
        }
        return count >= 2;
    }
}
