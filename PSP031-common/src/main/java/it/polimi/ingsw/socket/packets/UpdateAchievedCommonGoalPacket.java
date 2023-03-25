package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Type;

import java.util.List;

public record UpdateAchievedCommonGoalPacket(Type commonGoalType, List<String> playersAchieved) implements S2CPacket {
}
