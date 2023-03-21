package it.polimi.ingsw.model;

/**
 * 
 */
public class Board implements BoardView {
    private Property<Tile>[][] board;
    private final Property<Tile> invalidTile;

    /**
     * Default constructor
     */
    private Board(Property<Tile>[][] board) {
        this.board = board;
        invalidTile = new PropertyImpl<Tile>(new Tile(Color.GREEN));
    }

    @Override
    public Property<Tile> tile(int r, int c) {
        if(board[r][c]==invalidTile) throw new IndexOutOfBoundsException( "Invalid Position selected");
        else return board[r][c];
    }

}