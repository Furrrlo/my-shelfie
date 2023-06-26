package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.rmi.RmiLobbyController;
import it.polimi.ingsw.server.controller.LobbyServerController;

import java.rmi.RemoteException;
import java.util.function.Consumer;

/**
 * RMI remotable object which is used to implement {@link LobbyController}.
 * <p>
 * This object is per-player, meaning that a new one will be created and exported
 * for each player. An object is then in charge of reading RMI {@link LobbyController}
 * requests from its assigned player and relaying them to the protocol-agnostic
 * {@link LobbyServerController}.
 *
 * @see RmiLobbyController
 */
public class RmiLobbyServerController implements RmiLobbyController {

    private final String nick;
    private final LobbyServerController controller;
    private final Consumer<Throwable> disconnectHandler;

    public RmiLobbyServerController(String nick, LobbyServerController controller, Consumer<Throwable> disconnectHandler) {
        this.nick = nick;
        this.controller = controller;
        this.disconnectHandler = disconnectHandler;
    }

    @Override
    public void setRequiredPlayers(int requiredPlayers) throws RemoteException {
        try {
            controller.setRequiredPlayers(nick, requiredPlayers);
        } catch (Throwable t) {
            disconnectHandler.accept(t);
        }
    }

    @Override
    public void ready(boolean ready) throws RemoteException {
        try {
            controller.ready(nick, ready);
        } catch (Throwable t) {
            disconnectHandler.accept(t);
        }
    }
}
