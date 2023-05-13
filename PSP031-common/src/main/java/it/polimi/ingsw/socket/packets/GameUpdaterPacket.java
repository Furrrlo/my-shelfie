package it.polimi.ingsw.socket.packets;

public sealed interface GameUpdaterPacket extends
        S2CPacket permits UpdateAchievedCommonGoalPacket, UpdateBoardTilePacket, UpdateCurrentTurnPacket, UpdateEndGamePacket,
                                          UpdateFirstFinisherPacket, UpdatePlayerConnectedPacket, UpdatePlayerScorePacket,
                                          UpdatePlayerShelfieTilePacket, UpdateSuspendedPacket {
}
