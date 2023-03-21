package it.polimi.ingsw.model;

public class Shelfie implements ShelfieView {

    public static final int ROW = 6;
    public static final int COLUMN = 5;

    private final Property<Tile>[][] shelfie;

    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked") // Java doesn't support generic arrays creation
    public Shelfie() {
        shelfie = new PropertyImpl[ROW][COLUMN];
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COLUMN; c++) {
                shelfie[r][c] = null;
            }
        }
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        return shelfie[r][c];
    }
}