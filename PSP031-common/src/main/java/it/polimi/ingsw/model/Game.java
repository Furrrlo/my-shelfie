package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Game implements GameView {

    private final int gameID;
    private final Board board;
    private final List<Player> players;
    private final Property<Player> currentTurn;
    private final PersonalGoal personalGoal;
    private final List<CommonGoal> commonGoal;
    private final Property<@Nullable Player> firstFinisher;

    public Game(int gameID,
                Board board,
                List<Player> players,
                Player currentTurn,
                List<CommonGoal> commonGoal,
                PersonalGoal personalGoal) {
        this.gameID = gameID;
        this.board = board;
        this.players = players;
        this.currentTurn = new PropertyImpl<>(currentTurn);
        this.personalGoal = personalGoal;
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
    public List<Player> getPlayers() {
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
    public List<CommonGoal> getCommonGoals() {
        return commonGoal;
    }

    @Override
    public Property<@Nullable Player> firstFinisher() {
        return firstFinisher;
    }
}