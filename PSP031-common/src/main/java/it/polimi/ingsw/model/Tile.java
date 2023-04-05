package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public class Tile implements Serializable {

    private final Color color;
    private final int picIndex;

    /**
     * generate tile with given color
     */
    public Tile(Color color) {
        Random random = new Random();
        int[] indexes = new int[] { 0, 1, 2, 3 };
        this.picIndex = indexes[random.nextInt(indexes.length)];
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