package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/** Read-only object which represent the shelfie of a player in an in-progress game */
public interface ShelfieView extends Serializable {

    int ROWS = 6;
    int COLUMNS = 5;

    /**
     * Returns the tile in position r {@literal &} c ('r' specifying the row and 'c' the column)
     */
    Provider<@Nullable Tile> tile(int r, int c);

    Stream<? extends TileAndCoords<? extends Provider<@Nullable Tile>>> tiles();

    /** returns the number of free tiles of a specified column of the calling shelfie */
    int getColumnFreeSpace(int col);

    /**
     * returns true if all the tiles of the shelfie calling the method overlaps with equal not null tiles of the
     * shelfie (that) passed as a parameter
     */
    boolean isOverlapping(ShelfieView that);

    /**
     * returns the amount of tiles correctly positioned according to the personal goal passed as parameter
     */
    int numTilesOverlappingWithPersonalGoal(PersonalGoalView personalGoal);

    /**
     * returns a list of sub-lists containing all the distinct group of tiles that can be identified in the calling shelfie
     */
    List<List<TileAndCoords<Tile>>> groupsOfTiles();

    /** returns true if the free space in a given column of the shelfie is grater or equal then selected */
    boolean checkColumnSpace(int shelfCol, int selected);

    /** Returns true if this shelfie has no more free space (not even for a single tile) */
    boolean isFull();
}
