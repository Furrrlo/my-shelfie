package it.polimi.ingsw.model;

import it.polimi.ingsw.BoardCoord;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public interface BoardView extends Serializable {
    int BOARD_ROWS = 9;
    int BOARD_COLUMNS = 9;

    int getRows();

    int getCols();

    Provider<@Nullable Tile> tile(int r, int c);

    Stream<? extends TileAndCoords<? extends Provider<@Nullable Tile>>> tiles();

    /**
     * returns a int[][] where elements as '1' specifies valid positions for tiles, while elements as '0'
     * specifies invalid positions for tiles
     */
    int[][] getValidTiles();

    /**
     * returns true if board is empty
     */
    boolean isEmpty();

    /**
     * returns true if the tiles specified by the coords in selected can be picked according to the game rules
     * 
     * @param selected : list of BoardCoord ( index of coords for tiles on Bord )
     */
    boolean checkBoardCoord(List<BoardCoord> selected);

}
