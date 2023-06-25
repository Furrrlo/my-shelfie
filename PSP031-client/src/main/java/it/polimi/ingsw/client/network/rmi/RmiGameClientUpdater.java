package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.client.updater.GameClientUpdater;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.rmi.RmiGameUpdater;

/**
 * RMI GameClientUpdater implementation which is exported as a remotable object
 * which will be given to the server
 *
 * @see GameClientUpdater
 * @see it.polimi.ingsw.controller
 */
class RmiGameClientUpdater extends GameClientUpdater implements RmiGameUpdater {

    public RmiGameClientUpdater(Game game) {
        super(game);
    }
}
