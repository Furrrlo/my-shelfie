package it.polimi.ingsw.model;

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
                shelfie[r][c] = null;
            }
        }
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        return shelfie[r][c];
    }
}