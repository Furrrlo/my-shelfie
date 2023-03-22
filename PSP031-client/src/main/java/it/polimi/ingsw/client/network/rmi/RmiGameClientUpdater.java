package it.polimi.ingsw.client.network.rmi;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.rmi.RmiGameUpdater;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

class RmiGameClientUpdater implements RmiGameUpdater {

    private final Game game;

    public RmiGameClientUpdater(Game game) {
        this.game = game;
    }

    private Player findPlayerBy(String nick) {
        return game.getPlayers().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow();
    }

    private CommonGoal findCommonGoalBy(Type commonGoalType) {
        return game.getCommonGoals().stream()
                .filter(goal -> goal.getType() == commonGoalType)
                .findFirst()
                .orElseThrow();
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't handle generics correctly
    public void updateBoardTile(int row, int col, @Nullable Tile tile) throws RemoteException {
        game.getBoard().tile(row, col).set(tile);
    }

    @Override
    @SuppressWarnings("NullAway") // NullAway doesn't handle generics correctly
    public void updatePlayerShelfieTile(String nick, int row, int col, @Nullable Tile tile) throws RemoteException {
        findPlayerBy(nick).getShelfie().tile(row, col).set(tile);
    }

    @Override
    public void updateCurrentTurn(String nick) throws RemoteException {
        game.currentTurn().set(findPlayerBy(nick));
    }

    @Override
    public void updateFirstFinisher(String nick) throws RemoteException {
        game.firstFinisher().set(findPlayerBy(nick));
    }

    @Override
    public void updateAchievedCommonGoal(Type commonGoalType, List<String> playersAchieved) throws RemoteException {
        findCommonGoalBy(commonGoalType).achieved().set(playersAchieved.stream()
                .map(this::findPlayerBy)
                .collect(Collectors.toList()));
    }
}
