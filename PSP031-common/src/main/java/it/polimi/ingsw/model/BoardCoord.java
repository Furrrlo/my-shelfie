package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Represents the coordinates of a cell on the board
 *
 * @param row the row of this coord
 * @param col the col of this coord
 */
public record BoardCoord(int row, int col) implements Coord, Serializable {
}
