package it.polimi.ingsw.model;

/** Base interface for anything which implements a coordinate */
public interface Coord {

    /** Returns the row of this coord */
    int col();

    /** Returns the col of this coord */
    int row();
}
