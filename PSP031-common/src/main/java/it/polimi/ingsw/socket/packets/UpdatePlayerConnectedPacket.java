package it.polimi.ingsw.socket.packets;

public record UpdatePlayerConnectedPacket(String nick, boolean connected) implements GameUpdaterPacket {
}
