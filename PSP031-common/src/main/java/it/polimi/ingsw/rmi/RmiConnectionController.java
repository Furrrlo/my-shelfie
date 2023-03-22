package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiConnectionController extends Remote {

    String REMOTE_NAME = "my_shelfie_rmi";

    void joinGame(String nick, RmiLobbyUpdaterFactory updaterFactory) throws RemoteException;
}
