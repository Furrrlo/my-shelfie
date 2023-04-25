package it.polimi.ingsw.controller;

import com.google.errorprone.annotations.Immutable;
import it.polimi.ingsw.model.CommonGoalChecker;
import it.polimi.ingsw.model.Shelfie;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.model.ShelfieView.COLUMNS;
import static it.polimi.ingsw.model.ShelfieView.ROWS;

@Immutable
public class FourQuadripletsCommonGoalChecker implements CommonGoalChecker {

    record Index(int r, int c) {
    }

    @Override
    public boolean checkCommonGoal(Shelfie shelfie) {
        int count = 0;
        int[][] checked = new int[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (checked[r][c] == 0 && shelfie.tile(r, c).get() != null) {
                    if (getQuadrupletCheck(shelfie, r, c, checked, count + 1) >= 4)
                        count++;
                }
            }
        }
        return count >= 4;
    }

    /**
     * Returns the number of adjacent tiles to the one specified by given row and col, marking them if more than 4
     * with the number of quadriplet they belong to (specified by parameter marker), if not, marks them with -1
     * to ensure they don't get inspected further
     **/
    public int getQuadrupletCheck(Shelfie shelfie, int row, int col, int[][] checked, int marker) {
        List<Index> indexes = new ArrayList<>();
        indexes.add(new Index(row, col));
        checked[row][col] = marker;
        int prevSize;
        do {
            prevSize = indexes.size();
            for (int i = 0; i < indexes.size(); i++) {
                Index curr = indexes.get(i);
                Tile currTile = shelfie.tile(curr.r, curr.c).get();
                if (curr.r < ROWS - 1
                        && Objects.equals(shelfie.tile(curr.r + 1, curr.c).get(), currTile)
                        && !indexes.contains(new Index(curr.r + 1, curr.c))) {
                    indexes.add(new Index(curr.r + 1, curr.c));
                    checked[curr.r + 1][curr.c] = marker;
                }
                if (curr.c < COLUMNS - 1
                        && Objects.equals(shelfie.tile(curr.r, curr.c + 1).get(), currTile)
                        && !indexes.contains(new Index(curr.r, curr.c + 1))) {
                    indexes.add(new Index(curr.r, curr.c + 1));
                    checked[curr.r][curr.c + 1] = marker;
                }
                if (curr.r > 0
                        && shelfie.tile(curr.r - 1, curr.c) == shelfie.tile(curr.r, curr.c)
                        && !indexes.contains(new Index(curr.r - 1, curr.c))) {
                    indexes.add(new Index(curr.r - 1, curr.c));
                    checked[curr.r - 1][curr.c] = marker;
                }
                if (curr.c > 0
                        && Objects.equals(shelfie.tile(curr.r, curr.c - 1).get(), currTile)
                        && !indexes.contains(new Index(curr.r, curr.c - 1))) {
                    indexes.add(new Index(curr.r, curr.c - 1));
                    checked[curr.r][curr.c - 1] = marker;
                }
            }
        } while (indexes.size() > prevSize);
        if (indexes.size() < 4)
            for (Index i : indexes)
                checked[i.r][i.c] = -1;
        return indexes.size();
    }
}
