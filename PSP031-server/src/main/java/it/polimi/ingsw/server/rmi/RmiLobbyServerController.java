package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.rmi.RmiLobbyController;
import it.polimi.ingsw.server.controller.LobbyServerController;

import java.rmi.RemoteException;

public class RmiLobbyServerController implements RmiLobbyController {

    private final String nick;
    private final LobbyServerController controller;

    public RmiLobbyServerController(String nick, LobbyServerController controller) {
        this.nick = nick;
        this.controller = controller;
    }

    @Override
    public void ready(boolean ready) throws RemoteException {
        controller.ready(nick, ready);
    }
}
