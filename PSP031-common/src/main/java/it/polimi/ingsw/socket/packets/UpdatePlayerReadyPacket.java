package it.polimi.ingsw.socket.packets;

public record UpdatePlayerReadyPacket(String nick, boolean ready) implements LobbyUpdaterPacket {
}
