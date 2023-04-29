package it.polimi.ingsw.socket.packets;

/**
 * Packet that can be sent in response to a {@link JoinGamePacket}
 */
public sealed interface JoinResponsePacket extends S2CAckPacket permits LobbyPacket, NickNotValidPacket {
}
