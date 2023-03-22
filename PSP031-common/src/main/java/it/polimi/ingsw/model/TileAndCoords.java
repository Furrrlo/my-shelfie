package it.polimi.ingsw.model;

import java.io.Serializable;

public record TileAndCoords<P>(P tile, int row, int col) implements Serializable {
}
