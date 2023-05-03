package it.polimi.ingsw.socket.packets;

public record RequiredPlayersPacket(int requiredPlayers) implements LobbyActionPacket {
}
