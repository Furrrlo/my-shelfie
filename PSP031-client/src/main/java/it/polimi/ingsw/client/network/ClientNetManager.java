package it.polimi.ingsw.client.network;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.LobbyView;

public interface ClientNetManager {

    LobbyView joinGame(String nick) throws Exception;

    void ready(boolean ready) throws DisconnectedException;
}
