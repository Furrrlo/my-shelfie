package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Read-only interface of a personal goal.
 * <p>
 * A personal goal is a goal which is specific to a player in the game
 * and can only be seen and achieved by them.
 */
public interface PersonalGoalView extends Serializable {

    int ROWS = Shelfie.ROWS;
    int COLUMNS = Shelfie.COLUMNS;

    /** Returns the index of this personal goal used as an identifier for assets */
    int getIndex();

    /**
     * Returns the tile in position r {@literal &} c
     * 
     * @param r defines row of personalGoal
     * @param c defines column of personalGoal
     * @return tile of personal Goal
     */
    @Nullable
    Tile get(int r, int c);

    /** Returns whether the personal goal was achieved (all the tiles where successfully matched) */
    boolean achievedPersonalGoal(ShelfieView shelfie);

    /** Returns a stream of the tiles which make up this personal goal and their coords */
    Stream<TileAndCoords<@Nullable Tile>> tiles();
}
