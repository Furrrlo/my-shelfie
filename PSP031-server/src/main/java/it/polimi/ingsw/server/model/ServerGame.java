package it.polimi.ingsw.server.model;

import org.jetbrains.annotations.Nullable;
import it.polimi.ingsw.model.*;


import java.util.*;

public class ServerGame implements ServerGameView {

    private final int gameID;
    private final Board board;
    private final List<Tile> bag;
    private final List<ServerPlayer> players;
    private final Property<ServerPlayer> currentTurn;
    private final List<ServerCommonGoal> commonGoal;
    private final Property<@Nullable ServerPlayer> firstFinisher;

    public ServerGame(int gameID,
                      Board board,
                      List<Tile> bag,
                      List<ServerPlayer> players,
                      ServerPlayer currentTurn,
                      List<ServerCommonGoal> commonGoal) {
        this.gameID = gameID;
        this.board = board;
        this.bag = bag;
        this.players = players;
        this.currentTurn = new PropertyImpl<>(currentTurn);
        this.commonGoal = commonGoal;
        this.firstFinisher = PropertyImpl.nullableProperty(null);
    }

    @Override
    public int getGameID() {
        return gameID;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public List<Tile> getBag() {
        return bag;
    }

    @Override
    public List<ServerPlayer> getPlayers() {
        return players;
    }

    @Override
    public Property<ServerPlayer> currentTurn() {
        return currentTurn;
    }

    @Override
    public List<ServerCommonGoal> getCommonGoals() {
        return commonGoal;
    }

    @Override
    public Property<ServerPlayer> firstFinisher() {
        return firstFinisher;
    }
}