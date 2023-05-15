package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.rmi.RmiGameController;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.model.ServerPlayer;

import java.rmi.RemoteException;
import java.util.List;

public class RmiGameServerController implements RmiGameController {

    private final ServerPlayer player;
    private final GameServerController controller;

    public RmiGameServerController(ServerPlayer player, GameServerController controller) {
        this.player = player;
        this.controller = controller;
    }

    @Override
    public void makeMove(List<BoardCoord> selected, int shelfCol) throws RemoteException {
        controller.makeMove(player, selected, shelfCol);
    }

    @Override
    public void sendMessage(String message, String nickReceivingPlayer) throws RemoteException {
        controller.sendMessage(player.getNick(), message, nickReceivingPlayer);
    }
}
