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

    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public Shelfie(@Nullable Color[][] tiles) {
        this.shelfie = Arrays.stream(tiles)
                .map(row -> Arrays.stream(row)
                        .map(color -> color == null ? SerializableProperty.nullableProperty(null)
                                : SerializableProperty.nullableProperty(new Tile(color)))
                        .toArray(Property[]::new))
                .toArray(Property[][]::new);
    }

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
                    msg.append("***");
                } else {
                    Color color = Objects.requireNonNull(this.tile(row, col).get()).getColor();
                    if (color.equals(Color.BLUE))
                        msg.append(ConsoleColors.CYAN).append(ConsoleColors.BLUE_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.GREEN))
                        msg.append(ConsoleColors.GREEN).append(ConsoleColors.GREEN_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.ORANGE))
                        msg.append(ConsoleColors.YELLOW_BRIGHT).append(ConsoleColors.ORANGE_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.PINK))
                        msg.append(ConsoleColors.PURPLE).append(ConsoleColors.PURPLE_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.YELLOW))
                        msg.append(ConsoleColors.ORANGE).append(ConsoleColors.YELLOW_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                    if (color.equals(Color.LIGHTBLUE))
                        msg.append(ConsoleColors.BLUE).append(ConsoleColors.CYAN_BACKGROUND_BRIGHT).append("***")
                                .append(ConsoleColors.RESET);
                }
            }
            System.out.println(msg);
        }
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
        if (!(o instanceof Shelfie shelfie1))
            return false;
        return Arrays.deepEquals(shelfie, shelfie1.shelfie);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(shelfie);
    }

    @Override
    public String toString() {
        return "Shelfie{" +
                "shelfie=" + Arrays.toString(shelfie) +
                '}';
    }
}