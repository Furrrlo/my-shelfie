package it.polimi.ingsw.socket.packets;

/** Interface used to identify packets used for {@link it.polimi.ingsw.updater.LobbyUpdater}s communications */
public sealed interface LobbyUpdaterPacket extends S2CPacket
                                           permits CreateGamePacket, UpdateJoinedPlayerPacket, UpdatePlayerReadyPacket,
                                           UpdateRequiredPlayersPacket {
}
