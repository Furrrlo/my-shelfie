package it.polimi.ingsw.client.network;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.model.LobbyView;

public interface ClientNetManager {

    String getHost();

    int getPort();

    LobbyAndController<? extends LobbyView> joinGame(String nick) throws Exception;
}
