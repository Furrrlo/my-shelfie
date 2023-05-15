package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.BoardCoord;

import java.util.List;

public record MakeMovePacket(List<BoardCoord> selected, int shelfCol) implements C2SPacket, GameActionPacket {
}
