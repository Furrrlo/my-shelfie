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
}