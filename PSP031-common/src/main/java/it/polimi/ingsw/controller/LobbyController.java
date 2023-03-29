package it.polimi.ingsw.controller;

import it.polimi.ingsw.DisconnectedException;

public interface LobbyController {

    void ready(boolean ready) throws DisconnectedException;
}
