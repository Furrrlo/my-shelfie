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

    private final static int[][] TWO_PLAYERS_MATRIX = new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };
    private final static int[][] THREE_PLAYERS_MATRIX = new int[][] {
            { 0, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0 }
    };
    private final static int[][] FOUR_PLAYERS_MATRIX = new int[][] {
            { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 0, 0, 0 }
    };

    private final Property<@Nullable Tile>[][] board;
    private final Property<Tile> invalidTile;

    @SuppressWarnings({
            "unchecked", // Arrays don't support generics and need unchecked casts
            "ReferenceEquality" // It's done on purpose to check for invalid tiles
    })
    public Board(int numOfPlayers) {
        var invalidTile = new Tile(Color.GREEN);
        this.invalidTile = new SerializableProperty<>(invalidTile);
        this.board = Arrays.stream(generateBasedOnPlayers(numOfPlayers, invalidTile))
                .map(row -> Arrays.stream(row)
                        .map(tile -> tile == invalidTile ? this.invalidTile : SerializableProperty.nullableProperty(tile))
                        .toArray(Property[]::new))
                .toArray(Property[][]::new);
    }

    /**
     * depending on the number of players calls the method responsible for generating the board
     */
    private static @Nullable Tile[][] generateBasedOnPlayers(int numOfPlayers, Tile invalidTile) {
        return switch (numOfPlayers) {
            case 2 -> generateBoard(TWO_PLAYERS_MATRIX, invalidTile);
            case 3 -> generateBoard(THREE_PLAYERS_MATRIX, invalidTile);
            case 4 -> generateBoard(FOUR_PLAYERS_MATRIX, invalidTile);
            default -> throw new UnsupportedOperationException("Invalid player number (min: 2, max: 4): " + numOfPlayers);
        };
    }

    /**
     * @param validTiles : matrix of type int whose elements represents if a tile is valid(1) or invalid(0)
     * @param invalidTile : specifies the parameter for setting all the invalid tiles in the board
     *        returns an empty board with invalid tiles positioned as specified in validTiles
     */
    private static @Nullable Tile[][] generateBoard(int[][] validTiles, Tile invalidTile) {
        Tile[][] board = new Tile[BOARD_ROWS][BOARD_COLUMNS];
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLUMNS; c++) {
                if (validTiles[r][c] == 0) {
                    board[r][c] = invalidTile;
                } else {
                    board[r][c] = null;
                }
            }
        }
        return board;
    }

    @Override
    public int getRows() {
        return BOARD_ROWS;
    }

    @Override
    public int getCols() {
        return BOARD_COLUMNS;
    }

    @Override
    public boolean isValidTile(int r, int c) {
        return r >= 0 && r < getRows() && c >= 0 && c < getCols() && board[r][c] != invalidTile;
    }

    @Override
    public Property<@Nullable Tile> tile(int r, int c) {
        // Force an AIOB if r or c are not between 0 and ROWS/COLS
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
    public boolean isEmpty() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                if (isValidTile(i, j) && board[i][j] != null)
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean needsRefill() {
        return tiles()
                .filter(t -> t.tile().get() != null)
                .allMatch(t -> (!isValidTile(t.row() + 1, t.col()) || tile(t.row() + 1, t.col()).get() == null) &&
                        (!isValidTile(t.row() - 1, t.col()) || tile(t.row() - 1, t.col()).get() == null) &&
                        (!isValidTile(t.row(), t.col() + 1) || tile(t.row(), t.col() + 1).get() == null) &&
                        (!isValidTile(t.row(), t.col() - 1) || tile(t.row(), t.col() - 1).get() == null));
    }

    //TODO: test everything!!
    @Override
    public boolean checkBoardCoord(List<BoardCoord> selected) {
        if (selected.size() == 0 || selected.size() > 3)
            return false;

        // If any coord is specified more than once, it's invalid
        if (selected.stream().distinct().count() != selected.size())
            return false;

        for (BoardCoord coord : selected) {
            if (!isValidTile(coord.row(), coord.col()))
                return false;
            if (tile(coord.row(), coord.col()).get() == null)
                return false;
            if (!hasFreeSide(coord.row(), coord.col()))
                return false;
        }

        if (selected.size() == 1)
            return true;
        if (selected.size() == 2)
            return hasCommonSide(selected.get(0).row(), selected.get(0).col(), selected.get(1).row(), selected.get(1).col());
        if (selected.size() == 3)
            return hasCommonSide(
                    selected.get(0).row(), selected.get(0).col(),
                    selected.get(1).row(), selected.get(1).col(),
                    selected.get(2).row(), selected.get(2).col());
        return true;

    }

    @Override
    public boolean hasFreeSide(int row, int col) {
        if (!isValidTile(row + 1, col) || tile(row + 1, col).get() == null)
            return true;
        if (!isValidTile(row - 1, col) || tile(row - 1, col).get() == null)
            return true;
        if (!isValidTile(row, col + 1) || tile(row, col + 1).get() == null)
            return true;
        return !isValidTile(row, col - 1) || tile(row, col - 1).get() == null;
    }

    /** returns true if the two tiles in the specified positions have one common side */
    private boolean hasCommonSide(int row0, int col0, int row1, int col1) {
        if (row0 == row1 && (col1 == col0 + 1 || col1 == col0 - 1))
            return true;
        return col0 == col1 && (row1 == row0 + 1 || row1 == row0 - 1);
    }

    /** returns true if the three tiles in the specified positions are linked between them in a line */
    private boolean hasCommonSide(int row0, int col0, int row1, int col1, int row2, int col2) {
        if ((row0 == row1 && row1 == row2) || (col0 == col1 && col1 == col2)) {
            return (hasCommonSide(row0, col0, row1, col1) && hasCommonSide(row1, col1, row2, col2)) ||
                    (hasCommonSide(row0, col0, row2, col2) && hasCommonSide(row1, col1, row2, col2)) ||
                    (hasCommonSide(row1, col1, row0, col0) && hasCommonSide(row0, col0, row2, col2));
        }
        return false;
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
}