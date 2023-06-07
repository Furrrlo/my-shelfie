package it.polimi.ingsw.rmi;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.updater.LobbyUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI remotable service which will be used to implement {@link LobbyUpdaterFactory}
 *
 * This re-declares the same methods, but with an RMI compatible signature, throwing
 * {@link RemoteException} instead o {@link DisconnectedException}.
 * <p>
 * The {@link Adapter} is then used in order to be able to have an actual {@link LobbyUpdaterFactory}
 * interface implementation
 *
 * @see LobbyUpdaterFactory
 * @see Adapter
 */
public interface RmiLobbyUpdaterFactory extends Remote {

    /** RMI redeclaration of {@link LobbyUpdaterFactory#create(LobbyAndController)}, check that for docs and details */
    RmiLobbyUpdater create(LobbyAndController<Lobby> lobby) throws RemoteException;

    class Adapter extends RmiAdapter implements LobbyUpdaterFactory {

        private final RmiLobbyUpdaterFactory updater;

        public Adapter(RmiLobbyUpdaterFactory updater) {
            this.updater = updater;
        }

        @Override
        public LobbyUpdater create(LobbyAndController<Lobby> lobby) throws DisconnectedException {
            return adapt(() -> new RmiLobbyUpdater.Adapter(updater.create(lobby)));
        }
    }
}
