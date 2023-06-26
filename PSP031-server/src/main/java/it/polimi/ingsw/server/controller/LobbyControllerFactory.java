package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.LobbyController;

/**
 * Factory object which constructs a protocol-specific controller.
 *
 * @see #create(LobbyServerController)
 */
public interface LobbyControllerFactory {

    /**
     * Constructs a protocol-specific controller given the protocol-agnostic
     * server controller of a lobby.
     * <p>
     * Constructed objects will be in charge of relaying actions of a specific user
     * (the one which they were constructed for) to the given {@code lobbyServerController}
     * and sending the results back.
     * 
     * @param lobbyServerController generic server controller of a lobby
     * @return protocol-specific controller
     * @throws DisconnectedException if anything goes wrong during the construction
     */
    LobbyController create(LobbyServerController lobbyServerController) throws DisconnectedException;
}
