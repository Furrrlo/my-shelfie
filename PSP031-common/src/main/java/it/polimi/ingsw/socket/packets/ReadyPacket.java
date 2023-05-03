package it.polimi.ingsw.socket.packets;

public record ReadyPacket(boolean ready) implements LobbyActionPacket {
}
