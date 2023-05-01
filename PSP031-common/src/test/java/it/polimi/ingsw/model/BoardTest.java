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
    void testBoardException1() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class, () -> new Board(5),
                "Invalid player number (min: 2, max: 4): " + 5);
        assertTrue(("Invalid player number (min: 2, max: 4): " + 5).contentEquals(thrown.getMessage()));
    }

    @Test
    void testBoardException2() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class, () -> new Board(1),
                "Invalid player number (min: 2, max: 4): " + 1);
        assertTrue(("Invalid player number (min: 2, max: 4): " + 1).contentEquals(thrown.getMessage()));
    }

    @Test
    void testBoardException3() {
        assertDoesNotThrow(() -> new Board(2));
    }

    @Test
    void testBoardException4() {
        assertDoesNotThrow(() -> new Board(3));
    }

    @Test
    void testBoardException5() {
        assertDoesNotThrow(() -> new Board(4));
    }

    @Test
    void IsEmpty_newBoardAlwaysEmpty_True() {
        //new board is empty by definition
        var board = new Board(3);
        assertTrue(board.isEmpty());
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
        board.refillBoardRandom();
        assertFalse(board.isEmpty());
    }

    @Test
    void isEmpty_boardAfterRefill() {
        //refilled board is not empty by definition
        var board = new Board(2);
        board.refillBoardRandom();
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
        board.refillBoardBag(selected);
        /* board now refilled with just one tile */
        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_ifTilesAreNotLinked_False() {
        /*
         * private final static int[][] TWO_PLAYERS_MATRIX = new int[][] {
         * 1 2 3 4 5 6 7 8 9
         * 1 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 3 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 4 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 6 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 7 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 8 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 9 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * };
         */
        var board = new Board(2);
        List<BoardCoord> bc = new ArrayList<>();
        bc.add(new BoardCoord(3, 4));
        bc.add(new BoardCoord(4, 3));
        //tiles are not linked
        board.refillBoardCoord(bc);
        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_ifTilesAreLinked_True() {
        /*
         * private final static int[][] TWO_PLAYERS_MATRIX = new int[][] {
         * 1 2 3 4 5 6 7 8 9
         * 1 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
         * 3 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 4 { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
         * 6 { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
         * 7 { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
         * 8 { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
         * 9 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * };
         */
        var board = new Board(2);
        List<BoardCoord> bc = new ArrayList<>();
        bc.add(new BoardCoord(3, 4));
        bc.add(new BoardCoord(2, 4));
        //tiles are  linked
        board.refillBoardCoord(bc);
        assertFalse(board.needsRefill());
    }

    /*
     * @Test
     * void checkBoardCoord1True(){
     * 
     * }
     * 
     * @Test
     * void checkBoardCoord1False(){
     * 
     * }
     * 
     * @Test
     * void hasFreeSidesTrue(){
     * 
     * }
     * 
     * @Test
     * void hasFreeSidesFalse(){
     * 
     * }
     * 
     * @Test
     * void hasCommonSidesTrue(){
     * 
     * }
     * 
     * @Test
     * void hasCommonSidesFalse(){
     * 
     * }
     * 
     */
}