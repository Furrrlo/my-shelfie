package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public class Game implements GameView {

    private final int gameID;
    private final Board board;
    private final @Unmodifiable List<Player> players;
    private final Property<Player> currentTurn;
    private final PersonalGoal personalGoal;
    private final @Unmodifiable List<CommonGoal> commonGoal;
    private final Property<@Nullable Player> firstFinisher;

    public Game(int gameID,
                Board board,
                List<Player> players,
                int currentTurnPlayerIdx,
                List<CommonGoal> commonGoal,
                PersonalGoal personalGoal,
                @Nullable Integer firstFinisherId) {
        this.gameID = gameID;
        this.board = board;
        this.players = List.copyOf(players);
        this.currentTurn = new SerializableProperty<>(players.get(currentTurnPlayerIdx));
        this.personalGoal = personalGoal;
        this.commonGoal = List.copyOf(commonGoal);
        this.firstFinisher = SerializableProperty
                .nullableProperty(firstFinisherId == null ? null : players.get(firstFinisherId));
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
    public @Unmodifiable List<Player> getPlayers() {
        return players;
    }

    @Override
    public Property<Player> currentTurn() {
        return currentTurn;
    }

    @Override
    public PersonalGoal getPersonalGoal() {
        return personalGoal;
    }

    @Override
    public @Unmodifiable List<CommonGoal> getCommonGoals() {
        return commonGoal;
    }

    @Override
    public Property<@Nullable Player> firstFinisher() {
        return firstFinisher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Game game))
            return false;
        return gameID == game.gameID &&
                board.equals(game.board) &&
                players.equals(game.players) &&
                currentTurn.get().equals(game.currentTurn.get()) &&
                personalGoal.equals(game.personalGoal) &&
                commonGoal.equals(game.commonGoal) &&
                Objects.equals(firstFinisher.get(), game.firstFinisher.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, board, players, currentTurn.get(), personalGoal, commonGoal, firstFinisher.get());
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID=" + gameID +
                ", board=" + board +
                ", players=" + players +
                ", currentTurn=" + currentTurn +
                ", personalGoal=" + personalGoal +
                ", commonGoal=" + commonGoal +
                ", firstFinisher=" + firstFinisher +
                '}';
    }
}