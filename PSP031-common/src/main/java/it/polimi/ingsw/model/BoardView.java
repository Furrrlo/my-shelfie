package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public interface BoardView extends Serializable {
    int BOARD_ROWS = 9;
    int BOARD_COLUMNS = 9;

    int getRows();

    int getCols();

    /**
     * Returns whether the given position can be occupied by a tile and, therefore,
     * {@link #tile(int, int)} will not throw an exception when invoked
     * 
     * @return true if the given pos can be occupied by a tile
     */
    boolean isValidTile(int r, int c);

    Provider<@Nullable Tile> tile(int r, int c);

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
