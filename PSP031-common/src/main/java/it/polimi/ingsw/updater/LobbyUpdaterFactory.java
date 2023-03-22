package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Lobby;

public interface LobbyUpdaterFactory {

    LobbyUpdater create(Lobby lobby) throws DisconnectedException;
}
