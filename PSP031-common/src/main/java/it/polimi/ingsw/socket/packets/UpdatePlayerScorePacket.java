package it.polimi.ingsw.socket.packets;

public record UpdatePlayerScorePacket(String nick, int score) implements GameUpdaterPacket {
}
