package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Objects;

public class Tile implements Serializable {

    private final Color color;
    private final int picIndex;

    /**
     * generate tile with given color
     */
    public Tile(Color color) {
        this(color, 0);
    }

    public Tile(Color color, int picIndex) {
        this.picIndex = picIndex;
        this.color = color;

    }

    /** Return color of specified tile */
    public Color getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tile tile))
            return false;
        return color == tile.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "color=" + color +
                '}';
    }

    public int getPicIndex() {
        return picIndex;
    }
}