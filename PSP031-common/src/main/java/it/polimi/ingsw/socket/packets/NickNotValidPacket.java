package it.polimi.ingsw.socket.packets;

/** Signals that a nick is already in use */
public record NickNotValidPacket(String message) implements JoinResponsePacket {
}
