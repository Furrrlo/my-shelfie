package it.polimi.ingsw.rmi;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** RMI remotable service clients can use to establish a connection with the server */
public interface RmiConnectionController extends Remote {

    /** Remote name used to identify {@link RmiConnectionController} in a {@link java.rmi.registry.Registry} */
    String REMOTE_NAME = "my_shelfie_rmi";

    /**
     * Returns the host address of the client invoking this method remotely
     * <p>
     * This can be used by clients to discover which address (and therefore which network interface)
     * can successfully communicate with the other party, and therefore bind stuff on that address
     * 
     * @return the host address of the client invoking this
     * @throws RemoteException if anything fails and the connection is lost
     */
    String getClientAddressHost() throws RemoteException;

    /**
     * Initiates the handshake process with the other party, providing a nickname to be used as an identifier
     * and a means of keeping the connection alive
     * 
     * @param nick nick to be used as an identifier for this client
     * @param heartbeatHandler means of keeping the connection alive and detecting potential disconnects
     * @return the remote service exclusive to this player which can be used to join games
     * @throws RemoteException if anything fails and the connection is lost
     * @throws NickNotValidException if the {@code nick} is already in use by another player
     */
    ConnectedController doConnect(String nick, RmiHeartbeatHandler heartbeatHandler)
            throws RemoteException, NickNotValidException;

    /**
     * RMI remotable service exclusive to a single authenticated client which can be used by said client
     * to join games
     */
    interface ConnectedController extends Remote {

        /**
         * Ask the server to join a game
         * <p>
         * The server is in charge of choosing the most appropriate lobby to put this player in.
         * <p>
         * The lobby will be sent to the player by invoking {@link RmiLobbyUpdaterFactory#create(LobbyAndController)}
         * on the provided {@code updaterFactory} exactly once.
         *
         * @param updaterFactory factory used to exchange {@link LobbyAndController} and {@link LobbyUpdater}
         * @throws RemoteException if anything fails and the connection is lost
         * @see it.polimi.ingsw.updater.LobbyUpdaterFactory
         */
        void joinGame(RmiLobbyUpdaterFactory updaterFactory) throws RemoteException;
    }
}
