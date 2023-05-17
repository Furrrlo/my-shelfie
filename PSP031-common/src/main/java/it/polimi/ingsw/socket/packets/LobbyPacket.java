package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Lobby;

/** Ack packet with a {@link Lobby} */
public record LobbyPacket(Lobby lobby) implements S2CAckPacket {
}
