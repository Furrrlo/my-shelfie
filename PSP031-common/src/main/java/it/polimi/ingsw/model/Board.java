package it.polimi.ingsw.model;

import it.polimi.ingsw.BoardCoord;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @implNote Java Serialization automatically takes care of keeping multiple references to a single object
 *           correct on deserialization:
 *           "Multiple references to a single object are encoded using a reference sharing mechanism so that
 *           graphs of objects can be restored to the same shape as when the original was written."
 *           Using invalidTile's reference as a marker should be deserialized correctly.
 * @see java.io.ObjectOutputStream
 */
public class Board implements BoardView {

    private final Property<@Nullable Tile>[][] board;
    private final Property<Tile> invalidTile;

    @SuppressWarnings("unchecked")
    public Board(int numOfPlayers) {
        this.board = Arrays.stream(generateBasedOnPlayers(numOfPlayers))
                .map(row -> Arrays.stream(row).map(SerializableProperty::nullableProperty).toArray(Property[]::new))
                .toArray(Property[][]::new);
        invalidTile = new SerializableProperty<>(new Tile(Color.GREEN));
    }

    private static @Nullable Tile[][] generateBasedOnPlayers(int numOfPlayers) {
        return new Tile[9][9]; // TODO: generate based on number of players
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
    public Property<@Nullable Tile> tile(int r, int c) {
        if (board[r][c] == invalidTile)
            throw new IndexOutOfBoundsException("Invalid Position selected");
        return board[r][c];
    }

    @Override
    public Stream<TileAndCoords<Property<@Nullable Tile>>> tiles() {
        return IntStream.range(0, getRows()).boxed().flatMap(row -> IntStream.range(0, getCols()).boxed()
                .filter(col -> board[row][col] != invalidTile)
                .map(col -> new TileAndCoords<>(board[row][col], row, col)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Board that))
            return false;
        return IntStream.range(0, getRows()).boxed()
                .allMatch(row -> IntStream.range(0, getCols()).boxed()
                        .allMatch(col -> Objects.equals(board[row][col].get(), that.board[row][col].get())));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(board)
                .mapToInt(row -> Arrays.stream(row)
                        .mapToInt(tile -> Objects.hashCode(tile.get()))
                        .reduce(1, (a, b) -> 31 * a + b))
                .reduce(1, (a, b) -> 31 * a + b);
    }

    @Override
    public String toString() {
        return "Board{" +
                "board=" + Arrays.deepToString(board) +
                ", invalidTile=" + invalidTile +
                '}';
    }

    public void placeTile(int row, int col, Tile tile) {
        board[row][col] = new SerializableProperty<>(tile);
        //TODO: check if it's right
    }

    public void removeTile(int row, int col) {
        board[row][col] = null;
    }

    public void refillBoard() {
        //TODO: implement
    }

    public boolean isEmpty() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                if (board[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: test everything!!
    public boolean checkBoardCoord(List<BoardCoord> selected) {

        for (BoardCoord coord : selected) {
            if (!hasFreeSide(coord.row(), coord.col())) {
                return false;
            }
            if (tile(coord.row(), coord.col()).get() == null) {
                return false;
            }
        }

        if (selected.size() == 1) {
            return true;
        }
        if (selected.size() == 2) {
            return hasCommonSide(selected.get(0).row(), selected.get(0).col(), selected.get(1).row(), selected.get(1).col());
        }
        if (selected.size() == 3) {
            return hasCommonSide(selected.get(0).row(), selected.get(0).col(), selected.get(1).row(), selected.get(1).col(),
                    selected.get(2).row(), selected.get(2).col());
        }
        return true;

    }

    public boolean hasFreeSide(int row, int col) {
        if (Objects.equals(tile(row + 1, col), invalidTile)) {
            return true;
        }
        if (Objects.equals(tile(row - 1, col), invalidTile)) {
            return true;
        }
        if (Objects.equals(tile(row, col + 1), invalidTile)) {
            return true;
        }
        if (Objects.equals(tile(row, col - 1), invalidTile)) {
            return true;
        }
        return false;

    }

    public boolean hasCommonSide(int row0, int col0, int row1, int col1) {
        if (row0 == row1 && (col1 == col0 + 1 || col1 == col0 - 1)) {
            return true;
        }
        if (col0 == col1 && (row1 == row0 + 1 || row1 == row0 - 1)) {
            return true;
        }

        return false;

    }

    public boolean hasCommonSide(int row0, int col0, int row1, int col1, int row2, int col2) {
        if (row0 == row1 && row1 == row2 || col0 == col1 && col1 == col2) {
            return hasCommonSide(row0, col0, row1, col1) && hasCommonSide(row1, col1, row2, col2) ||
                    hasCommonSide(row0, col0, row2, col2) && hasCommonSide(row1, col1, row2, col2) ||
                    hasCommonSide(row1, col1, row0, col0) && hasCommonSide(row0, col0, row2, col2);
        }
        return false;

    }

}