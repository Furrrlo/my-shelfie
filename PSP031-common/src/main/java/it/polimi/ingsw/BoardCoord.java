package it.polimi.ingsw;

import it.polimi.ingsw.model.Coord;

import java.io.Serializable;

public record BoardCoord(int row, int col) implements Coord, Serializable {
}
