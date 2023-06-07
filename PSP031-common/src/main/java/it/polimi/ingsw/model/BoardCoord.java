package it.polimi.ingsw.model;

import java.io.Serializable;

public record BoardCoord(int row, int col) implements Coord, Serializable {
}
