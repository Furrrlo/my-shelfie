package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerGame implements ServerGameView {

    private final int gameID;
    private final Board board;
    private final List<Tile> bag;
    private final @UnmodifiableView List<Tile> bagView;
    private final @Unmodifiable List<ServerPlayer> players;
    private final ServerPlayer startingPlayer;
    private final Property<ServerPlayer> currentTurn;
    private final @Unmodifiable List<ServerCommonGoal> commonGoal;
    private final Property<@Nullable ServerPlayer> firstFinisher;

    /**
     * @param bag the starting bad of tiles, which will be copied by the constructor
     */
    public ServerGame(int gameID,
                      Board board,
                      List<Tile> bag,
                      List<ServerPlayer> players,
                      int startingPlayerIdx,
                      List<ServerCommonGoal> commonGoal,
                      Property<@Nullable ServerPlayer> firstFinisher) {
        this.gameID = gameID;
        this.board = board;
        this.bag = new ArrayList<>(bag);
        this.bagView = Collections.unmodifiableList(this.bag);
        this.players = List.copyOf(players);
        this.startingPlayer = players.get(startingPlayerIdx);
        this.currentTurn = new SerializableProperty<>(players.get(startingPlayerIdx));
        this.commonGoal = List.copyOf(commonGoal);
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
    public @UnmodifiableView List<Tile> getBagView() {
        return bagView;
    }

    public List<Tile> getBag() {
        return bag;
    }

    @Override
    public @Unmodifiable List<ServerPlayer> getPlayers() {
        return players;
    }

    @Override
    public ServerPlayer getStartingPlayer() {
        return startingPlayer;
    }

    @Override
    public Property<ServerPlayer> currentTurn() {
        return currentTurn;
    }

    @Override
    public @Unmodifiable List<ServerCommonGoal> getCommonGoals() {
        return commonGoal;
    }

    @Override
    public Property<@Nullable ServerPlayer> firstFinisher() {
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
                startingPlayer.equals(that.startingPlayer) &&
                currentTurn.get().equals(that.currentTurn.get()) &&
                commonGoal.equals(that.commonGoal) &&
                Objects.equals(firstFinisher.get(), that.firstFinisher.get()) &&
                players.equals(that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, board, bag, players, startingPlayer, currentTurn.get(), commonGoal, firstFinisher.get());
    }

    @Override
    public String toString() {
        return "ServerGame{" +
                "gameID=" + gameID +
                ", board=" + board +
                ", bag=" + bag +
                ", players=" + players +
                ", startingPlayer=" + startingPlayer +
                ", currentTurn=" + currentTurn +
                ", commonGoal=" + commonGoal +
                ", firstFinisher=" + firstFinisher +
                '}';
    }
}