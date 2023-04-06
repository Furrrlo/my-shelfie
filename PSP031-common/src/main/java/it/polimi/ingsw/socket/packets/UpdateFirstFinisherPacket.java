package it.polimi.ingsw.socket.packets;

import org.jetbrains.annotations.Nullable;

public record UpdateFirstFinisherPacket(@Nullable String nick) implements GameUpdaterPacket {
}
