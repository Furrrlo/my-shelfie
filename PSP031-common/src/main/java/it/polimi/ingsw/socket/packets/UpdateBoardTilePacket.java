package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

public record UpdateBoardTilePacket(int row, int col, @Nullable Tile tile) implements GameUpdaterPacket {
}
