package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

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
    void boardWrongNumOfPlayers5() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class, () -> new Board(5),
                "Invalid player number (min: 2, max: 4): " + 5);
        assertTrue(("Invalid player number (min: 2, max: 4): " + 5).contentEquals(thrown.getMessage()));
    }

    @Test
    void boardWrongNumOfPlayers1() {
        UnsupportedOperationException thrown = assertThrows(
                UnsupportedOperationException.class, () -> new Board(1),
                "Invalid player number (min: 2, max: 4): " + 1);
        assertTrue(("Invalid player number (min: 2, max: 4): " + 1).contentEquals(thrown.getMessage()));
    }

    @Test
    void boarRightNumOfPlayers2() {
        assertDoesNotThrow(() -> new Board(2));
    }

    @Test
    void boarRightNumOfPlayers3() {
        assertDoesNotThrow(() -> new Board(3));
    }

    @Test
    void boarRightNumOfPlayers4() {
        assertDoesNotThrow(() -> new Board(4));
    }
    /*
     * @Test
     * void boardIsEmptyTrue(){
     * assertEquals();
     * }
     * 
     * @Test
     * void boardIsEmptyFalse(){
     * assertNotEquals();
     * }
     * 
     * @Test
     * void boardIsNotEmptyTrue(){
     * assertEquals();
     * }
     * 
     * @Test
     * void boardIsNotEmptyFalse(){
     * assertNotEquals();
     * }
     * 
     * @Test
     * void needsRefillTrue(){
     * assertEquals();
     * }
     * 
     * @Test
     * void needsRefillFalse(){
     * assertNotEquals();
     * }
     * 
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