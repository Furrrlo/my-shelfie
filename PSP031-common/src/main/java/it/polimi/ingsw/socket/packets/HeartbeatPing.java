package it.polimi.ingsw.socket.packets;

import java.time.Instant;

public record HeartbeatPing(Instant serverTime) implements S2CPacket {
}
