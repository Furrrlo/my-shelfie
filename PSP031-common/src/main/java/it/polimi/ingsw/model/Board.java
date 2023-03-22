package it.polimi.ingsw.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * @implNote Java Serialization automatically takes care of keeping multiple references to a single object
 *           correct on deserialization:
 *           "Multiple references to a single object are encoded using a reference sharing mechanism so that
 *           graphs of objects can be restored to the same shape as when the original was written."
 *           Using invalidTile's reference as a marker should be deserialized correctly.
 * @see java.io.ObjectOutputStream
 */
public class Board implements BoardView {

    private final Property<Tile>[][] board;
    private final Property<Tile> invalidTile;

    @SuppressWarnings("unchecked")
    public Board(int numOfPlayers) {
        this.board = Arrays.stream(generateBasedOnPlayers(numOfPlayers))
                .map(row -> Arrays.stream(row).map(PropertyImpl::new).toArray(Property[]::new))
                .toArray(Property[][]::new);
        invalidTile = new PropertyImpl<>(new Tile(Color.GREEN));
    }

    private static Tile[][] generateBasedOnPlayers(int numOfPlayers) {
        return new Tile[20][20]; // TODO: generate based on number of players
    }

    @Override
    public int getRows() {
        return board.length;
    }

    @Override
    public int getCols() {
        return board.length == 0 ? 0 : board[0].length;
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        if (board[r][c] == invalidTile)
            throw new IndexOutOfBoundsException("Invalid Position selected");
        else return board[r][c];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board board1)) return false;
        return Arrays.deepEquals(board, board1.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "Board{" +
                "board=" + Arrays.toString(board) +
                ", invalidTile=" + invalidTile +
                '}';
    }
}