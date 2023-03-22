package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.stream.Stream;

public interface PersonalGoalView extends Serializable {

    int ROWS = Shelfie.ROWS;
    int COLUMNS = Shelfie.COLUMNS;

    /**
     * @param r defines row of personalGoal
     * @param c defines column of personalGoal
     * @return tile of personal Goal
     */
    Tile get(int r, int c);

    Stream<TileAndCoords<Tile>> tiles();
}
