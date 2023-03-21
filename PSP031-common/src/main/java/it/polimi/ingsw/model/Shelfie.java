package it.polimi.ingsw.model;

import java.util.Arrays;

public class Shelfie implements ShelfieView {

    private final Property<Tile>[][] shelfie;

    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked") // Java doesn't support generic arrays creation
    public Shelfie() {
        shelfie = new PropertyImpl[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                shelfie[r][c] = PropertyImpl.nullableProperty(null);
            }
        }
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        return shelfie[r][c];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelfie shelfie1)) return false;
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