package it.polimi.ingsw.socket.packets;

public record UpdateRequiredPlayersPacket(int requiredPlayers) implements LobbyUpdaterPacket {
}
