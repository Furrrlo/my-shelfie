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

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#TWO_ALL_DIFF_COLUMNS}
 */
@Immutable
public class TwoAllDiffColumnsCommonGoalChecker implements CommonGoalChecker {

    /**
     * Returns the number of different colors present in a given color c of a shelfie, excluding null tiles
     */
    public int numColorsForColumn(Shelfie shelfie, int[][] checked, int marker, int c) {
        List<Color> count = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            if (shelfie.tile(r, c).get() != null
                    && !count.contains(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor()))
                count.add(Objects.requireNonNull(shelfie.tile(r, c).get()).getColor());
        }
        if (count.size() == 6)
            for (int r = 0; r < ROWS; r++)
                checked[r][c] = marker;
        return count.size();
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int count = 0;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int c = 0; c < COLUMNS; c++) {
            if (numColorsForColumn(shelfie, checked, count + 1, c) == ROWS)
                count++;
        }
        return count >= 2;
    }
}
