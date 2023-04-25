package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Game implements GameView {

    private final int gameID;
    private final Board board;
    private final @Unmodifiable List<Player> players;
    private final Player thePlayer;
    private final Player startingPlayer;
    private final Property<Player> currentTurn;
    private final PersonalGoal personalGoal;
    private final @Unmodifiable List<CommonGoal> commonGoal;
    private final Property<@Nullable Player> firstFinisher;

    public Game(int gameID,
                Board board,
                List<PlayerFactory> playersFactories,
                int thePlayerIdx,
                int startingPlayerIdx,
                int currentTurnIdx,
                Function<@Unmodifiable List<Player>, List<CommonGoal>> commonGoalFactory,
                PersonalGoal personalGoal,
                @Nullable Integer firstFinisherIdx) {
        // There's a circular dependency between the players and these properties, create proxies first
        var currentTurnProxy = new PropertyProxy<Player>();
        var firstFinisherProxy = new PropertyProxy<Player>();
        // Now we can create the actual players
        this.players = IntStream.range(0, playersFactories.size())
                .mapToObj(idx -> playersFactories.get(idx).create(
                        idx == startingPlayerIdx,
                        player -> currentTurnProxy.map(currentTurnPlayer -> {
                            // This should be the only plane where the currentTurn null value might spill
                            Objects.requireNonNull(
                                    currentTurnPlayer,
                                    "Something went wrong during while solving Game circular dependency");
                            return currentTurnPlayer.equals(player);
                        }),
                        player -> firstFinisherProxy.map(firstFinisher -> Objects.equals(firstFinisher, player))))
                .toList();
        // Once we have players, we can set the properties to their actual value
        currentTurnProxy.setProxied(new SerializableProperty<>(players.get(currentTurnIdx)));
        this.currentTurn = currentTurnProxy;

        firstFinisherProxy.setProxied(SerializableProperty.nullableProperty(firstFinisherIdx == null
                ? null
                : players.get(firstFinisherIdx)));
        this.firstFinisher = firstFinisherProxy;

        // We can set the rest
        this.gameID = gameID;
        this.board = board;
        this.personalGoal = personalGoal;
        this.thePlayer = players.get(thePlayerIdx);
        this.startingPlayer = players.get(startingPlayerIdx);
        this.commonGoal = List.copyOf(commonGoalFactory.apply(players));
    }

    public interface PlayerFactory {

        Player create(boolean isStartingPlayer,
                      Function<Player, Provider<Boolean>> isCurrentTurnFactory,
                      Function<Player, Provider<Boolean>> isFirstFinisherFactory);
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
    public Player thePlayer() {
        return thePlayer;
    }

    @Override
    public Player getStartingPlayer() {
        return startingPlayer;
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
                thePlayer.equals(game.thePlayer) &&
                startingPlayer.equals(game.startingPlayer) &&
                currentTurn.get().equals(game.currentTurn.get()) &&
                personalGoal.equals(game.personalGoal) &&
                commonGoal.equals(game.commonGoal) &&
                Objects.equals(firstFinisher.get(), game.firstFinisher.get()) &&
                players.equals(game.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, board, players, thePlayer, startingPlayer, currentTurn.get(), personalGoal, commonGoal,
                firstFinisher.get());
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID=" + gameID +
                ", board=" + board +
                ", players=" + players +
                ", thePlayer=" + thePlayer +
                ", startingPlayer=" + startingPlayer +
                ", currentTurn=" + currentTurn +
                ", personalGoal=" + personalGoal +
                ", commonGoal=" + commonGoal +
                ", firstFinisher=" + firstFinisher +
                '}';
    }
}