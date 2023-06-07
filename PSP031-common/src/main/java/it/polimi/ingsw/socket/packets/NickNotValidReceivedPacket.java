package it.polimi.ingsw.socket.packets;

/** Ack packet to signal that the {@link it.polimi.ingsw.controller.NickNotValidException} was received */
public record NickNotValidReceivedPacket() implements C2SAckPacket {
}
