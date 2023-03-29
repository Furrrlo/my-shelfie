package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.model.Lobby;

public interface LobbyUpdaterFactory {

    LobbyUpdater create(LobbyAndController<Lobby> lobby) throws DisconnectedException;
}
