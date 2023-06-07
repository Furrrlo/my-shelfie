package it.polimi.ingsw.socket.packets;

/** Interface used to identify packets used for {@link it.polimi.ingsw.updater.GameUpdater}s communications */
public sealed interface GameUpdaterPacket extends
        S2CPacket permits UpdateAchievedCommonGoalPacket, UpdateBoardTilePacket, UpdateCurrentTurnPacket, UpdateEndGamePacket,
                                          UpdateFirstFinisherPacket, UpdateMessagePacket, UpdatePlayerConnectedPacket,
                                          UpdatePlayerScorePacket, UpdatePlayerShelfieTilePacket, UpdateSuspendedPacket {
}
