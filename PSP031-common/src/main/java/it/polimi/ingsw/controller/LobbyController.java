package it.polimi.ingsw.controller;

import it.polimi.ingsw.DisconnectedException;

public interface LobbyController {

    void setRequiredPlayers(int requiredPlayers) throws DisconnectedException;

    void ready(boolean ready) throws DisconnectedException;
}
