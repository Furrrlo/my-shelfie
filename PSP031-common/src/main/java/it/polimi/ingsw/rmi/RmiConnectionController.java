package it.polimi.ingsw.rmi;

import it.polimi.ingsw.NickNotValidException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiConnectionController extends Remote {

    String REMOTE_NAME = "my_shelfie_rmi";

    String getClientAddressHost();

    ConnectedController doConnect(String nick, RmiHeartbeatHandler heartbeatHandler)
            throws RemoteException, NickNotValidException;

    interface ConnectedController extends Remote {

        void joinGame(RmiLobbyUpdaterFactory updaterFactory) throws RemoteException;
    }
}
