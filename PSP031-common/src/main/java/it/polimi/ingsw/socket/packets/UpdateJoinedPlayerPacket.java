package it.polimi.ingsw.socket.packets;

import java.util.List;

public record UpdateJoinedPlayerPacket(List<String> players) implements S2CPacket {
}
