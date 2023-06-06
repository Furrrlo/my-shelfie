package it.polimi.ingsw.updater;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.model.Lobby;

/** Factory pattern in charge of creating an updater for a given model + controller pair */
public interface LobbyUpdaterFactory {

    /**
     * Create an updater for a given lobby model + controller pair
     *
     * @param lobby lobby modifiable model and controller to be used by the client
     * @return an instance of the updater which will be used to update the lobby
     * @throws DisconnectedException if the client disconnects during the call
     */
    LobbyUpdater create(LobbyAndController<Lobby> lobby) throws DisconnectedException;
}
