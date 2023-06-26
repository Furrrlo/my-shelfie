package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Modifiable implementation of {@link ShelfieView} */
public class Shelfie implements ShelfieView {

    private final Property<@Nullable Tile>[][] shelfie;

    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked") // Java doesn't support generic arrays creation
    public Shelfie() {
        shelfie = new SerializableProperty[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                shelfie[r][c] = SerializableProperty.nullableProperty(null);
            }
        }
    }

    /** returns a shelfie from a matrix of Color passed as parameter */
    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public Shelfie(@Nullable Color[][] tiles) {
        this.shelfie = Arrays.stream(tiles)
                .map(row -> Arrays.stream(row)
                        .map(color -> color == null
                                ? SerializableProperty.nullableProperty(null)
                                : SerializableProperty.nullableProperty(new Tile(color)))
                        .toArray(Property[]::new))
                .toArray(Property[][]::new);
    }

    @SuppressWarnings("unchecked")
    Shelfie(@Nullable Tile[][] tiles) {
        this.shelfie = Arrays.stream(tiles)
                .map(row -> Arrays.stream(row)
                        .map(tile -> tile == null
                                ? SerializableProperty.nullableProperty(null)
                                : SerializableProperty.nullableProperty(tile))
                        .toArray(Property[]::new))
                .toArray(Property[][]::new);
    }

    @Override
    public boolean isOverlapping(ShelfieView that) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (this.tile(r, c).get() != null &&
                        !Objects.equals(this.tile(r, c).get(), that.tile(r, c).get()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public int numTilesOverlappingWithPersonalGoal(PersonalGoalView personalGoal) {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (personalGoal.get(row, col) != null && this.tile(row, col).get() != null &&
                        Objects.equals(personalGoal.get(row, col), tile(row, col).get()))
                    count++;
            }
        }
        return count;
    }

    @Override
    public List<List<TileAndCoords<Tile>>> groupsOfTiles() {
        List<List<TileAndCoords<Tile>>> groupsOfTiles = new ArrayList<>();
        int[][] checked = new int[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (checked[r][c] == 0 && tile(r, c).get() != null) {
                    groupsOfTiles.add(this.groupOfTiles(r, c, checked));
                }
            }
        }
        return groupsOfTiles;
    }

    /**
     * returns a list af TileAndCoords of the same type that can be reached from a given position, and updates the
     * matrix of inspected types( checked[][] ) setting to 1 the elements in the same positions as the tiles added
     * to the group
     */
    private List<TileAndCoords<Tile>> groupOfTiles(int row, int col, int[][] checked) {
        List<TileAndCoords<Tile>> reachedTiles = new ArrayList<>();
        reachedTiles.add(new TileAndCoords<Tile>(this.tile(row, col).get(), row, col));
        checked[row][col] = 1;

        int prevSize;
        do {
            prevSize = reachedTiles.size();
            for (int i = 0; i < reachedTiles.size(); i++) {
                TileAndCoords<Tile> curr = reachedTiles.get(i);
                if (curr.row() < ROWS - 1
                        && Objects.equals(tile(curr.row() + 1, curr.col()).get(), curr.tile())
                        && !reachedTiles.contains(new TileAndCoords<Tile>(curr.tile(), curr.row() + 1, curr.col()))) {
                    reachedTiles.add(new TileAndCoords<Tile>(curr.tile(), curr.row() + 1, curr.col()));
                    checked[curr.row() + 1][curr.col()] = 1;
                }
                if (curr.col() < COLUMNS - 1
                        && Objects.equals(tile(curr.row(), curr.col() + 1).get(), curr.tile())
                        && !reachedTiles.contains(new TileAndCoords<Tile>(curr.tile(), curr.row(), curr.col() + 1))) {
                    reachedTiles.add(new TileAndCoords<Tile>(curr.tile(), curr.row(), curr.col() + 1));
                    checked[curr.row()][curr.col() + 1] = 1;
                }
                //check already covered by always checking first the tile over the one being analysed, therefore never entering
                //if second condition ( it is always already contained if it is equal )
                // if (curr.row() > 0
                //        && Objects.equals(tile(curr.row() - 1, curr.col()).get(), tile(curr.row(), curr.col()).get())
                //        && !reachedTiles.contains(new TileAndCoords<Tile>(curr.tile(), curr.row() - 1, curr.col()))) {
                //    reachedTiles.add(new TileAndCoords<Tile>(curr.tile(), curr.row() - 1, curr.col()));
                //    checked[curr.row() - 1][curr.col()] = 1;
                //}
                if (curr.col() > 0
                        && Objects.equals(tile(curr.row(), curr.col() - 1).get(), curr.tile())
                        && !reachedTiles.contains(new TileAndCoords<Tile>(curr.tile(), curr.row(), curr.col() - 1))) {
                    reachedTiles.add(new TileAndCoords<Tile>(curr.tile(), curr.row(), curr.col() - 1));
                    checked[curr.row()][curr.col() - 1] = 1;
                }
            }
        } while (reachedTiles.size() > prevSize);
        return reachedTiles;
    }

    @Override
    public Property<@Nullable Tile> tile(int r, int c) {
        return shelfie[r][c];
    }

    @Override
    public Stream<TileAndCoords<Property<@Nullable Tile>>> tiles() {
        return IntStream.range(0, ROWS).boxed().flatMap(row -> IntStream.range(0, COLUMNS).boxed()
                .map(col -> new TileAndCoords<>(shelfie[row][col], row, col)));
    }

    public int getColumnFreeSpace(int col) {
        int freeSpace = 0;
        for (int r = 0; r < ROWS; r++) {
            if (shelfie[r][col].get() == null) {
                freeSpace++;
            }
        }
        return freeSpace;
    }

    @Override
    public boolean checkColumnSpace(int shelfCol, int selected) {
        if (shelfCol < 0 || shelfCol >= COLUMNS)
            return false;
        return selected <= getColumnFreeSpace(shelfCol);
    }

    @Override
    public boolean isFull() {
        return tiles().allMatch(t -> t.tile().get() != null);
    }

    public void placeTiles(List<Tile> selectedTiles, int shelfCol) {
        //place tiles in the selected column from the bottom and first free space
        for (Tile tile : selectedTiles) {
            int freeSpace = getColumnFreeSpace(shelfCol);
            if (freeSpace > 0)
                shelfie[freeSpace - 1][shelfCol].set(tile);
            else
                throw new IndexOutOfBoundsException("This column is full");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Shelfie that))
            return false;
        return IntStream.range(0, ROWS).boxed()
                .allMatch(row -> IntStream.range(0, COLUMNS).boxed()
                        .allMatch(col -> Objects.equals(shelfie[row][col].get(), that.shelfie[row][col].get())));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(shelfie)
                .mapToInt(row -> Arrays.stream(row)
                        .mapToInt(tile -> Objects.hashCode(tile.get()))
                        .reduce(1, (a, b) -> 31 * a + b))
                .reduce(1, (a, b) -> 31 * a + b);
    }

    @Override
    public String toString() {
        return "Shelfie{" +
                "shelfie=" + Arrays.deepToString(shelfie) +
                '}';
    }

}