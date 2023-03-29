package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.rmi.*;
import it.polimi.ingsw.server.controller.ServerController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RmiConnectionServerController implements RmiConnectionController {

    private final ServerController controller;
    private @Nullable String nick;

    public static void bind(ServerController controller) throws RemoteException {
        bind(RmiConnectionController.REMOTE_NAME, controller);
    }

    @VisibleForTesting
    public static void bind(String remoteName,
                            ServerController controller)
            throws RemoteException {
        LocateRegistry.createRegistry(1099).rebind(
                remoteName,
                UnicastRemoteObjects.export(new RmiConnectionServerController(controller), 0));
    }

    private RmiConnectionServerController(ServerController controller) {
        this.controller = controller;
    }

    @Override
    public void joinGame(String nick,
                         RmiHeartbeatHandler handler,
                         RmiLobbyUpdaterFactory updaterFactory)
            throws RemoteException {
        this.nick = nick;
        controller.joinGame(
                nick,
                new RmiHeartbeatHandler.Adapter(handler),
                new RmiLobbyUpdaterFactory.Adapter(updaterFactory),
                controller -> {
                    try {
                        return new RmiLobbyController.Adapter(
                                UnicastRemoteObjects.export(new RmiLobbyServerController(nick, controller), 0));
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                    }
                },
                (player, game) -> {
                    try {
                        return new RmiGameController.Adapter(
                                UnicastRemoteObjects.export(new RmiGameServerController(player, game), 0));
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Unexpectedly failed to export RmiGameServerController", e);
                    }
                });
    }
}
