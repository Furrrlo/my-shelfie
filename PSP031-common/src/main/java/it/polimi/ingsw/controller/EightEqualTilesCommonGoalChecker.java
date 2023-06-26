package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;

import java.util.Arrays;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

/**
 * Object which can check whether a given {@link Shelfie} has achieved a common goal
 * of type {@link it.polimi.ingsw.model.Type#EIGHT_EQUAL_TILES}
 */
@Immutable
public class EightEqualTilesCommonGoalChecker implements CommonGoalChecker {

    /** Returns the amount of tiles of a given color present in the shelfie */
    public int equalColoredTiles(Shelfie shelfie, int[][] checked, Color color) {
        int count = 0;
        int[][] oldChecked = Arrays.stream(checked).map(int[]::clone).toArray(int[][]::new);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (shelfie.tile(r, c).get() != null
                        && Objects.requireNonNull(shelfie.tile(r, c).get()).getColor().equals(color)) {
                    count++;
                    checked[r][c] = count;
                }
            }
        }
        if (count < 8) {
            for (int r = 0; r < ROWS; r++)
                System.arraycopy(oldChecked[r], 0, checked[r], 0, COLUMNS);
        }
        return count;
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        boolean achieved = false;
        int[][] checked = new int[ROWS][COLUMNS];
        for (Color c : Color.values()) {
            if (equalColoredTiles(shelfie, checked, c) >= 8)
                achieved = true;
        }
        return achieved;
    }
}
