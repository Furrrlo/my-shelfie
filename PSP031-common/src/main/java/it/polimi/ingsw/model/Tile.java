package it.polimi.ingsw.model;

public class Tile {

    private final Color color;

    /**
     * generate tile with given color
     */
    public Tile(Color color) {
        this.color = color;
    }

    /**
     * @return color of specified tile
     */
    public Color getColor() {
        return this.color;
    }
}