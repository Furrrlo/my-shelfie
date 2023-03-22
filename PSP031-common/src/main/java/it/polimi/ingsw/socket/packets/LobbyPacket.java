package it.polimi.ingsw.socket.packets;

import it.polimi.ingsw.model.Lobby;

public record LobbyPacket(Lobby lobby) implements S2CPacket {
}
