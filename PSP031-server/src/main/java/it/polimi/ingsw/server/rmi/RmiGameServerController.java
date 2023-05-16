package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.rmi.RmiGameController;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Consumer;

public class RmiGameServerController implements RmiGameController {

    private final ServerPlayer player;
    private final GameServerController controller;
    private final Consumer<Throwable> disconnectHandler;

    public RmiGameServerController(ServerPlayer player, GameServerController controller,
                                   Consumer<Throwable> disconnectHandler) {
        this.player = player;
        this.controller = controller;
        this.disconnectHandler = disconnectHandler;
    }

    @Override
    public void makeMove(List<BoardCoord> selected, int shelfCol) throws RemoteException {
        try {
            controller.makeMove(player, selected, shelfCol);
        } catch (Throwable t) {
            disconnectHandler.accept(t);
        }
    }

    @Override
    public void sendMessage(String message, String nickReceivingPlayer) throws RemoteException {
        try {
            controller.sendMessage(player.getNick(), message, nickReceivingPlayer);
        } catch (Throwable t) {
            disconnectHandler.accept(t);
        }
    }
}
