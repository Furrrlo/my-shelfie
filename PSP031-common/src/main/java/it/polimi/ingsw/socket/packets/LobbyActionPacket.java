package it.polimi.ingsw.socket.packets;

/** Interface used to identify packets used for {@link it.polimi.ingsw.controller.LobbyController}s communications */
public sealed interface LobbyActionPacket extends C2SPacket permits ReadyPacket, RequiredPlayersPacket {
}
