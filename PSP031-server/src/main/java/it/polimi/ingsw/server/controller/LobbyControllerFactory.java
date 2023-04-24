package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.controller.LobbyController;

public interface LobbyControllerFactory {

    LobbyController create(LobbyServerController lobbyServerController);
}
