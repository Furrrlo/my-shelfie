package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    /** prints colored shelfie */
    @VisibleForTesting
    public void printColoredShelfie() {
        for (int row = 0; row < ROWS; row++) {
            StringBuilder msg = new StringBuilder();
            if (row == 0)
                msg.append("   1  2  3  4  5 \n");
            for (int col = 0; col < COLUMNS; col++) {
                if (col == 0)
                    msg.append(row + 1).append(" ");
                if (this.tile(row, col).get() == null) {
                    msg.append("| |");
                } else {
                    Color color = Objects.requireNonNull(this.tile(row, col).get()).getColor();
                    if (color.equals(Color.BLUE))
                        msg.append(ConsoleColors.CYAN).append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.GREEN))
                        msg.append(ConsoleColors.GREEN).append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.ORANGE))
                        msg.append(ConsoleColors.YELLOW_BRIGHT).append(ConsoleColors.ORANGE_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.PINK))
                        msg.append(ConsoleColors.PURPLE).append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.YELLOW))
                        msg.append(ConsoleColors.ORANGE).append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.LIGHTBLUE))
                        msg.append(ConsoleColors.BLUE).append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append("   ")
                                .append(ConsoleColors.RESET);
                }
            }
            System.out.println(msg);
        }
    }

    /**
     * returns true if all the tiles of the shelfie calling the method overlaps with equal not null tiles of the
     * shelfie (that) passed as a parameter
     */
    public boolean isOverlapping(Shelfie that) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (this.shelfie[r][c].get() != null &&
                        !Objects.equals(this.shelfie[r][c].get(), that.shelfie[r][c].get()))
                    return false;
            }
        }
        return true;
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