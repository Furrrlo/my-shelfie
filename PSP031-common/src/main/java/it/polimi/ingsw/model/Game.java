package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 
 */
public class Game implements GameView {

    private final List<Player> players;
    private final Board board;
    private final List<CommonGoal> commonGoal;
    private final int gameID;
    private final Property<@Nullable Player> firstFinisher;
    private final Property<Player> currentTurn;
    private final PersonalGoal personalGoal;

    @SuppressWarnings("NullAway")
    public Game(List<Player> players,
                Board board,
                List<CommonGoal> commonGoal,
                int gameID,
                Player currentTurn,
                PersonalGoal personalGoal) {
        this.players = players;
        this.board = board;
        this.commonGoal = commonGoal;
        this.gameID = gameID;
        this.firstFinisher = new PropertyImpl<>(null);
        this.currentTurn = new PropertyImpl<Player>(currentTurn);
        this.personalGoal = personalGoal;
    }

    @Override
    public List<Player> getPlayers() {
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
    public Property<Player> firstFinisher() {
        return firstFinisher;
    }

    @Override
    public Property<Player> currentTurn() {
        return currentTurn;
    }

    @Override
    public PersonalGoal getPersonalGoal() {
        return personalGoal;
    }

}