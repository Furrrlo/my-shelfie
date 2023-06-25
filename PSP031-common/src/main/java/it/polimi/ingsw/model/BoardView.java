package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public interface BoardView extends Serializable {
    int BOARD_ROWS = 9;
    int BOARD_COLUMNS = 9;

    /** Returns the number of rows of the board */
    int getRows();

    /** Returns the number of cols of the board */
    int getCols();

    /**
     * Returns whether the given position can be occupied by a tile and, therefore,
     * {@link #tile(int, int)} will not throw an exception when invoked
     * 
     * @return true if the given pos can be occupied by a tile
     */
    boolean isValidTile(int r, int c);

    /**
     * Returns the tile at the given coordinates
     *
     * @param r board row
     * @param c board col
     * @return the tile at the given coordinates
     * @throws IndexOutOfBoundsException if {@link #isValidTile(int, int)} returns false for the given coords
     */
    Provider<@Nullable Tile> tile(int r, int c);

    /** Returns a stream of all the tiles and their corresponding positions on the board */
    Stream<? extends TileAndCoords<? extends Provider<@Nullable Tile>>> tiles();

    /**
     * returns true if board is empty
     */
    boolean isEmpty();

    /** returns true if the board needs to be refilled */
    boolean needsRefill();

    /**
     * returns true if the tiles specified by the coords in selected can be picked according to the game rules
     * 
     * @param selected : list of BoardCoord ( index of coords for tiles on Bord )
     */
    boolean checkBoardCoord(List<? extends Coord> selected);

    /** Returns true if the board tile in specified position has at least one free side */
    boolean hasFreeSide(int row, int col);
}
