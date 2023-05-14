package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.UserMessage;
import org.jetbrains.annotations.Nullable;

public record UpdateMessagePacket(@Nullable UserMessage message) implements GameUpdaterPacket {
}
