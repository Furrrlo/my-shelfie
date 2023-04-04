package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerGame implements ServerGameView {

    private final int gameID;
    private final Board board;
    private final List<Tile> bag;
    private final List<ServerPlayer> players;
    private final Property<ServerPlayer> currentTurn;
    private final List<ServerCommonGoal> commonGoal;
    private final Property<@Nullable ServerPlayer> firstFinisher;

    /**
     * @param bag the starting bad of tiles, which will be copied by the constructor
     */
    public ServerGame(int gameID,
                      Board board,
                      List<Tile> bag,
                      List<ServerPlayer> players,
                      int currentTurnPlayerIdx,
                      List<ServerCommonGoal> commonGoal,
                      Property<@Nullable ServerPlayer> firstFinisher) {
        this.gameID = gameID;
        this.board = board;
        this.bag = new ArrayList<>(bag);
        this.players = players;
        this.currentTurn = new SerializableProperty<>(players.get(currentTurnPlayerIdx));
        this.commonGoal = commonGoal;
        this.firstFinisher = firstFinisher;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerGame that))
            return false;
        return gameID == that.gameID &&
                board.equals(that.board) &&
                bag.equals(that.bag) &&
                players.equals(that.players) &&
                currentTurn.equals(that.currentTurn) &&
                commonGoal.equals(that.commonGoal) &&
                firstFinisher.equals(that.firstFinisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, board, bag, players, currentTurn, commonGoal, firstFinisher);
    }

    @Override
    public String toString() {
        return "ServerGame{" +
                "gameID=" + gameID +
                ", board=" + board +
                ", bag=" + bag +
                ", players=" + players +
                ", currentTurn=" + currentTurn +
                ", commonGoal=" + commonGoal +
                ", firstFinisher=" + firstFinisher +
                '}';
    }
}