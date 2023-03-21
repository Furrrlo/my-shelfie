package it.polimi.ingsw.server.model;

import org.jetbrains.annotations.Nullable;
import it.polimi.ingsw.model.*;



import java.util.*;

/**
 * 
 */
public class ServerGame implements ServerGameView {

    private final List<ServerPlayer> players;
    private final Board board;
    private final List<CommonGoal> commonGoal;
    private final int gameID;
    private final Property<@Nullable ServerPlayer> firstFinisher;
    private final Property<ServerPlayer> currentTurn;
    private final List<Tile> bag;

    @SuppressWarnings("NullAway")
    public ServerGame(List<ServerPlayer> players,
                Board board,
                List<CommonGoal> commonGoal,
                int gameID,
                ServerPlayer currentTurn,
                List<Tile> bag) {
        this.players = players;
        this.board = board;
        this.commonGoal = commonGoal;
        this.gameID = gameID;
        this.firstFinisher = new PropertyImpl<>(null);
        this.currentTurn = new PropertyImpl<ServerPlayer>(currentTurn);
        this.bag=bag;
    }

    @Override
    public List<ServerPlayer> getPlayers() {
        return players;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public List<CommonGoal> getCommonGoal() {
        return commonGoal;
    }

    @Override
    public int getGameID() {
        return gameID;
    }

    @Override
    public Property<ServerPlayer> firstFinisher() {
        return firstFinisher;
    }

    @Override
    public Property<ServerPlayer> currentTurn() {
        return currentTurn;
    }

    @Override
    public List<Tile> getBag() {
        return bag;
    }


}