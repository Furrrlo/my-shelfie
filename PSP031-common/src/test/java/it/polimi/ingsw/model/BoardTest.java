package it.polimi.ingsw.model;

import it.polimi.ingsw.BoardCoord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testInvalidTile() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Board(2).tile(-1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new Board(2).tile(0, 0));
    }

    @Test
    void testValidTile() {
        assertDoesNotThrow(() -> {
            var b = new Board(2);
            b.tile(b.getRows() / 2, b.getCols() / 2);
        });
    }

    @Test
    void tiles() {
        assertDoesNotThrow(() -> new Board(2).tiles().forEach(t -> {
        }));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var board1 = new Board(2);
        final var board2 = new Board(2);

        assertEquals(board1, board1, "Same instance is not the same");
        assertNotEquals(board1, new Object(), "Different object should not be equals");
        assertEquals(board1, board2, "Instances with no differences should be equals");

        final var boardDiffTile = new Board(2);
        boardDiffTile.tile(boardDiffTile.getRows() / 2, boardDiffTile.getCols() / 2).set(new Tile(Color.BLUE));
        assertNotEquals(board1, boardDiffTile, "Instances with different tiles should not be equals");
    }

    @Test
    void testHashCode() {
        final var board1 = new Board(2);
        final var board2 = new Board(2);

        assertEquals(board1.hashCode(), board1.hashCode(), "Same instance is not the same");
        assertEquals(board1.hashCode(), board2.hashCode(), "Instances with no differences should be equals");

        final var boardDiffTile = new Board(2);
        boardDiffTile.tile(boardDiffTile.getRows() / 2, boardDiffTile.getCols() / 2).set(new Tile(Color.BLUE));
        assertNotEquals(board1.hashCode(), boardDiffTile.hashCode(), "Instances with different tiles should not be equals");
    }

    @Test
    void testToString() {
        var board = new Board(2);
        assertDoesNotThrow(board::toString);
    }

    @Test
    void boardIsInstanceOfBoard() {
        var board = new Board(2);
        assertInstanceOf(Board.class, board);
    }

    @Test
    void isValidTIle() {
        final var board = new Board(2);
        /*
         * TWO_PLAYERS_MATRIX
         ***** 0**1**2**3**4**5**6**7**8
         * 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 1 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 3 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 4 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 6 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 7 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 9 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * };
         */
        //testing board firs row 
        assertFalse(board.isValidTile(0, 0));
        assertFalse(board.isValidTile(0, 1));
        assertFalse(board.isValidTile(0, 2));
        assertFalse(board.isValidTile(0, 3));
        assertFalse(board.isValidTile(0, 4));
        assertFalse(board.isValidTile(0, 5));
        assertFalse(board.isValidTile(0, 6));
        assertFalse(board.isValidTile(0, 7));
        assertFalse(board.isValidTile(0, 8));
        //testing board second row 
        assertFalse(board.isValidTile(1, 0));
        assertFalse(board.isValidTile(1, 1));
        assertFalse(board.isValidTile(1, 2));
        assertTrue(board.isValidTile(1, 3));
        assertTrue(board.isValidTile(1, 4));
        assertFalse(board.isValidTile(1, 5));
        assertFalse(board.isValidTile(1, 6));
        assertFalse(board.isValidTile(1, 7));
        assertFalse(board.isValidTile(1, 8));

        //if r<0 or r >= BOARD.ROWS should return false
        assertFalse(board.isValidTile(-1, 0));
        assertFalse(board.isValidTile(9, 0));
        //if c<0 or c >= BOARD.COLUMNS should return false
        assertFalse(board.isValidTile(2, -1));
        assertFalse(board.isValidTile(2, 9));
    }

    @Test
    void testBoardException() {
        assertThrows(UnsupportedOperationException.class, () -> new Board(5),
                "Invalid player number (min: 2, max: 4): " + 5);
        assertThrows(UnsupportedOperationException.class, () -> new Board(1),
                "Invalid player number (min: 2, max: 4): " + 1);
        assertDoesNotThrow(() -> new Board(2));
        assertDoesNotThrow(() -> new Board(3));
        assertDoesNotThrow(() -> new Board(4));
    }

    @Test
    void IsEmpty_newBoardAlwaysEmpty_True() {
        //new board is empty by definition
        var board2 = new Board(2);
        assertTrue(board2.isEmpty());
        var board3 = new Board(3);
        assertTrue(board3.isEmpty());
        var board4 = new Board(4);
        assertTrue(board4.isEmpty());
    }

    @Test
    void isEmpty_ifBoardIsEmptyHasNoTiles() {
        var board = new Board(2);
        int count_tiles = 0;
        for (int r = 0; r < BoardView.BOARD_ROWS; r++) {
            for (int c = 0; c < BoardView.BOARD_COLUMNS; c++) {
                if (board.isValidTile(r, c) && board.tile(r, c).get() != null)
                    count_tiles++;
            }
        }
        assertEquals(board.isEmpty(), count_tiles == 0);
    }

    @Test
    void isEmpty_boardAfterRefill_False() {
        //refilled board is not empty by definition
        var board = new Board(2);
        Boards.refillBoardRandom(board);
        assertFalse(board.isEmpty());
    }

    @Test
    void isEmpty_boardAfterRefill() {
        //refilled board is not empty by definition
        var board = new Board(2);
        Boards.refillBoardRandom(board);
        int count_tiles = 0;
        for (int r = 0; r < BoardView.BOARD_ROWS; r++) {
            for (int c = 0; c < BoardView.BOARD_COLUMNS; c++) {
                if (board.isValidTile(r, c) && board.tile(r, c).get() != null)
                    count_tiles++;
            }
        }
        assertEquals(!board.isEmpty(), count_tiles > 0);
    }

    @Test
    void needsRefill_ifBoardIsEmpty_True() {
        var board = new Board(2);
        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_ifOneTileIsOnBoard_True() {
        List<Tile> selected = new ArrayList<>();
        selected.add(new Tile(Color.PINK));
        var board = new Board(2);
        Boards.refillBoardBag(board, selected);
        /* board now refilled with just one tile */
        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_ifTilesAreNotLinked_False() {
        /*
         * TWO_PLAYERS_MATRIX
         ****** 0**1**2**3**4**5**6**7**8
         * 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 1 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 3 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 4 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 6 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 7 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 9 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * };
         */
        var board = new Board(2);
        List<BoardCoord> bc = new ArrayList<>();
        bc.add(new BoardCoord(3, 4));
        bc.add(new BoardCoord(4, 3));
        //tiles are not linked
        Boards.refillBoardCoord(board, bc);
        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_ifTilesAreLinked_True() {
        /*
         * valid tiles for two players board
         ****** 0**1**2**3**4**5**6**7**8
         * 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 1 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 3 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 4 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 6 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 7 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 8 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         */
        var board = new Board(2);
        List<BoardCoord> bc = new ArrayList<>();
        bc.add(new BoardCoord(3, 4));
        bc.add(new BoardCoord(2, 4));
        //tiles are  linked
        Boards.refillBoardCoord(board, bc);
        assertFalse(board.needsRefill());
    }

    @Test
    void checkBoardCoord() {
        /*
         * valid tiles for two players board
         ****** 0**1**2**3**4**5**6**7**8
         * 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 1 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 3 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 4 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 6 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 7 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 8 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         */
        var board = new Board(2);
        Boards.refillBoardRandom(board);

        List<BoardCoord> selected = new ArrayList<>();

        //if selected size is 0, should return false
        assertFalse(board.checkBoardCoord(selected));
        selected.clear();

        //if selected size is >3, should return false 
        selected.add(new BoardCoord(1, 3));
        selected.add(new BoardCoord(1, 4));
        selected.add(new BoardCoord(4, 1));
        selected.add(new BoardCoord(5, 1));
        assertFalse(board.checkBoardCoord(selected));
        selected.clear();

        //if a tile is specified more than once should return false
        board.tiles()
                .filter(t -> board.tile(t.row(), t.col()).get() != null)
                .forEach(t -> assertFalse(
                        board.checkBoardCoord(List.of(new BoardCoord(t.row(), t.col()), new BoardCoord(t.row(), t.col())))));

        selected.add(new BoardCoord(1, 3));
        selected.add(new BoardCoord(1, 3));
        assertFalse(board.checkBoardCoord(selected));
        selected.clear();

        //if one of selected tile is invalid, should return false
        selected.add(new BoardCoord(0, 0));
        selected.add(new BoardCoord(1, 3));
        selected.add(new BoardCoord(1, 4));
        assertFalse(board.checkBoardCoord(selected));
        selected.clear();

        //if one of the tiles selected is null, should return false 
        Boards.emptyBoard(board);
        board.tiles()
                .filter(t -> t.tile().get() == null)
                .forEach(t -> assertFalse(board.checkBoardCoord(List.of(new BoardCoord(t.row(), t.col())))));
        Boards.refillBoardCoord(board, List.of(new BoardCoord(1, 3), new BoardCoord(1, 4)));
        selected.add(new BoardCoord(1, 3));
        selected.add(new BoardCoord(1, 4));
        selected.add(new BoardCoord(2, 3)); //null tile
        assertFalse(board.checkBoardCoord(selected));
        selected.clear();

        Boards.refillBoardRandom(board);

        //For all tiles without free sides, should return false
        board.tiles()
                .filter(t -> !board.hasFreeSide(t.row(), t.col())).forEach(
                        t -> assertFalse(board.checkBoardCoord(List.of(new BoardCoord(t.row(), t.col())))));

        //for all tiles with free sides, selected singularly should return true
        board.tiles()
                .filter(t -> board.hasFreeSide(t.row(), t.col())).forEach(
                        t -> assertTrue(board.checkBoardCoord(List.of(new BoardCoord(t.row(), t.col())))));

        //if selected size is 2, tiles are not the same, not null and have common side, should return true
        for (int row = 0; row < BoardView.BOARD_ROWS; row++) {
            for (int col = 0; col < BoardView.BOARD_COLUMNS; col++) {
                if (board.isValidTile(row, col) && board.tile(row, col).get() != null && board.hasFreeSide(row, col)) {
                    for (int row1 = 0; row1 < BoardView.BOARD_ROWS; row1++) {
                        for (int col1 = 0; col1 < BoardView.BOARD_COLUMNS; col1++) {
                            if (board.isValidTile(row1, col1) && board.tile(row1, col1).get() != null
                                    && board.hasCommonSide(row, col, row1, col1) && board.hasFreeSide(row1, col1))
                                assertTrue(
                                        board.checkBoardCoord(List.of(new BoardCoord(row, col), new BoardCoord(row1, col1))));
                        }
                    }
                }
            }
        }
        //if selected size is 3, tiles are not the same, not null and have common sides, should return true
        for (int row = 0; row < BoardView.BOARD_ROWS; row++) {
            for (int col = 0; col < BoardView.BOARD_COLUMNS; col++) {
                if (board.isValidTile(row, col) && board.tile(row, col).get() != null && board.hasFreeSide(row, col)) {
                    for (int row1 = 0; row1 < BoardView.BOARD_ROWS; row1++) {
                        for (int col1 = 0; col1 < BoardView.BOARD_COLUMNS; col1++) {
                            if (board.isValidTile(row1, col1) && board.tile(row1, col1).get() != null
                                    && board.hasCommonSide(row, col, row1, col1) && board.hasFreeSide(row1, col1) && row != row1
                                    && col != col1) {
                                for (int row2 = 0; row2 < BoardView.BOARD_ROWS; row2++) {
                                    for (int col2 = 0; col2 < BoardView.BOARD_COLUMNS; col2++) {
                                        if (board.isValidTile(row2, col2) && board.tile(row2, col2).get() != null
                                                && board.hasCommonSide(row, col, row1, col1, row2, col2)
                                                && board.hasFreeSide(row2, col2)
                                                && row2 != row1 && row2 != row && col1 != col2 && col != col2)
                                            assertTrue(board.checkBoardCoord(List.of(new BoardCoord(row, col),
                                                    new BoardCoord(row1, col1), new BoardCoord(row2, col2))));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Test
    void hasFreeSides() {
        var board = new Board(2);
        Boards.refillBoardRandom(board);
        List<BoardCoord> bc = new ArrayList<>();

        //no free sides
        bc.add(new BoardCoord(3, 2));
        bc.add(new BoardCoord(3, 4));
        bc.add(new BoardCoord(2, 3));
        bc.add(new BoardCoord(4, 3));
        Boards.refillBoardCoord(board, bc);
        assertFalse(board.hasFreeSide(3, 3));
        Boards.emptyBoard(board);
        bc.clear();

        //only one free invalid side
        bc.add(new BoardCoord(6, 4));
        bc.add(new BoardCoord(6, 5));
        bc.add(new BoardCoord(7, 3));
        bc.add(new BoardCoord(6, 5));
        Boards.refillBoardCoord(board, bc);
        assertTrue(board.hasFreeSide(6, 5));
    }

    @Test
    void hasCommonSides2() {
        var board = new Board(2);
        Boards.refillBoardRandom(board);
        List<BoardCoord> bc = new ArrayList<>();

        //2 in a row
        bc.add(new BoardCoord(1, 3));
        bc.add(new BoardCoord(1, 4));
        Boards.refillBoardCoord(board, bc);
        assertTrue(board.hasCommonSide(1, 3, 1, 4));
        Boards.emptyBoard(board);
        bc.clear();

        //2 in a column
        bc.add(new BoardCoord(1, 3));
        bc.add(new BoardCoord(2, 3));
        Boards.refillBoardCoord(board, bc);
        assertTrue(board.hasCommonSide(1, 3, 2, 3));
        Boards.emptyBoard(board);
        bc.clear();

        //2 in diagonal
        bc.add(new BoardCoord(1, 3));
        bc.add(new BoardCoord(2, 4));
        Boards.refillBoardCoord(board, bc);
        assertFalse(board.hasCommonSide(1, 3, 2, 4));
        Boards.emptyBoard(board);
        bc.clear();

    }

    @Test
    void hasCommonSides3() {
        var board = new Board(2);
        Boards.refillBoardRandom(board);
        List<BoardCoord> bc = new ArrayList<>();

        //3 in a row
        bc.add(new BoardCoord(2, 3));
        bc.add(new BoardCoord(2, 4));
        bc.add(new BoardCoord(2, 5));
        Boards.refillBoardCoord(board, bc);
        //coordinates given in order
        assertTrue(board.hasCommonSide(2, 3, 2, 4, 2, 5));
        //coordinates given in  reverse order
        assertTrue(board.hasCommonSide(2, 5, 2, 4, 2, 3));
        //coordinates given from middle one
        assertTrue(board.hasCommonSide(2, 4, 2, 3, 2, 5));
        Boards.emptyBoard(board);
        bc.clear();

        //3 in a column
        bc.add(new BoardCoord(3, 2));
        bc.add(new BoardCoord(4, 2));
        bc.add(new BoardCoord(5, 2));
        Boards.refillBoardCoord(board, bc);
        //coordinates given in order
        assertTrue(board.hasCommonSide(3, 2, 4, 2, 5, 2));
        //coordinates given in  reverse order
        assertTrue(board.hasCommonSide(5, 2, 4, 2, 3, 2));
        //coordinates given from middle one
        assertTrue(board.hasCommonSide(4, 2, 3, 2, 5, 2));
        Boards.emptyBoard(board);
        bc.clear();

        //3 in a L shape
        bc.add(new BoardCoord(3, 2));
        bc.add(new BoardCoord(4, 2));
        bc.add(new BoardCoord(4, 3));
        Boards.refillBoardCoord(board, bc);
        //coordinates given in order
        assertFalse(board.hasCommonSide(3, 2, 4, 2, 4, 3));
        Boards.emptyBoard(board);
        bc.clear();
    }
}