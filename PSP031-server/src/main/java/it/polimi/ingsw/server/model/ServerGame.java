package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

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
    private final Property<Boolean> endGame;
    private final Property<Boolean> suspended;
    private final Property<@Nullable UserMessage> message;

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
        this.endGame = new SerializableProperty<>(false);
        this.suspended = new SerializableProperty<>(false);
        this.message = SerializableProperty.nullableProperty(null);
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

    @VisibleForTesting
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
    public Property<Boolean> endGame() {
        return endGame;
    }

    @Override
    public Property<@Nullable UserMessage> message() {
        return message;
    }

    @Override
    public Property<Boolean> suspended() {
        return suspended;
    }

    public void refillBoard() {
        for (int r = 0; r < BoardView.BOARD_ROWS && this.bag.size() > 0; r++) {
            for (int c = 0; c < BoardView.BOARD_COLUMNS && this.bag.size() > 0; c++) {
                if (board.isValidTile(r, c)) {
                    Property<@Nullable Tile> tileProp = this.board.tile(r, c);
                    if (tileProp.get() == null)
                        tileProp.set(this.bag.remove(0));
                }
            }
        }
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
                players.equals(that.players) &&
                endGame.get().equals(that.endGame.get()) &&
                suspended.get().equals(that.suspended.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, board, bag, players, startingPlayer, currentTurn.get(), commonGoal, firstFinisher.get(),
                endGame.get(), suspended.get());
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
                ", endGame=" + endGame +
                ", suspended=" + suspended +
                '}';
    }
}