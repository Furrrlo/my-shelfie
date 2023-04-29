package it.polimi.ingsw.rmi;

import it.polimi.ingsw.NickNotValidException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiConnectionController extends Remote {

    String REMOTE_NAME = "my_shelfie_rmi";

    void joinGame(String nick,
                  RmiHeartbeatHandler heartbeatHandler,
                  RmiLobbyUpdaterFactory updaterFactory)
            throws RemoteException, NickNotValidException;
}
