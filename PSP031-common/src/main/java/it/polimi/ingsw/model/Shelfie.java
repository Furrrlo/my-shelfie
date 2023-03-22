package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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