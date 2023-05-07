package it.polimi.ingsw.socket.packets;

public record UpdateEndGamePacket(Boolean endGame) implements GameUpdaterPacket {
}
