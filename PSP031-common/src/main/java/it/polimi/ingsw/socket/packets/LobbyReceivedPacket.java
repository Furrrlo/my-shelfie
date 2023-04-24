package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Lobby;

/** Ack packet to signal the {@link Lobby} was received */
public record LobbyReceivedPacket() implements C2SAckPacket {
}
