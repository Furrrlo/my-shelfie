package it.polimi.ingsw.server.model;


/**
 * 
 */
public class Shelfie implements ShelfieView {

    private final Property<Tile>[][] shelfie;
    private static final int ROW = 6;
    private static final int COLUMN = 5;
    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked") // Java doesn't support generic arrays creation
    public Shelfie() {
        shelfie = new PropertyImpl[ROW][COLUMN];
        for(int r=0; r<6; r++) {
            for(int c=0; c<5; c++) {
                shelfie[r][c]=null;
            }
        }
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        return shelfie[r][c];
    }

}