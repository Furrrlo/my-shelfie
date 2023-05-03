package it.polimi.ingsw.socket.packets;

public sealed interface LobbyUpdaterPacket extends S2CPacket
                                           permits CreateGamePacket, UpdateJoinedPlayerPacket, UpdatePlayerReadyPacket,
                                           UpdateRequiredPlayersPacket {
}
