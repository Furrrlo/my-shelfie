package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.UserMessage;

public record UpdateMessagePacket(UserMessage message) implements GameUpdaterPacket {
}
