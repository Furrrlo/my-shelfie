package it.polimi.ingsw.socket.packets;

import java.time.Instant;

public record HeartbeatPong(Instant serverTime) implements C2SAckPacket {
}
