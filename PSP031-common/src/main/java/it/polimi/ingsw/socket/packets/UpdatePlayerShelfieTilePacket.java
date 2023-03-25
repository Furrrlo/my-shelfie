package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

public record UpdatePlayerShelfieTilePacket(String nick, int row, int col, @Nullable Tile tile) implements S2CPacket {
}
