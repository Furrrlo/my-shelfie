package it.polimi.ingsw.socket.packets;

/**
 * Pacchetto che può essere mandato in risposta a un {@link JoinGamePacket}
 */
public sealed interface JoinResponsePacket extends S2CAckPacket permits LobbyPacket, NickNotValidPacket {
}
