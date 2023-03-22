package it.polimi.ingsw.client.network;

import it.polimi.ingsw.model.LobbyView;

public interface ClientNetManager {

    LobbyView joinGame(String nick) throws Exception;
}
