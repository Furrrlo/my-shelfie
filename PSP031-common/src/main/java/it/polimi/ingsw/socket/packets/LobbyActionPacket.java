package it.polimi.ingsw.socket.packets;

public sealed interface LobbyActionPacket extends C2SPacket permits ReadyPacket, RequiredPlayersPacket {
}
