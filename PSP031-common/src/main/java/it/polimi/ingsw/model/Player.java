package it.polimi.ingsw.model;

import java.util.Objects;
import java.util.function.Function;

public class Player implements PlayerView {

    private final String nick;
    private final Shelfie shelfie;
    private final boolean isStartingPlayer;
    private final Property<Boolean> connected;
    private final Provider<Boolean> isCurrentTurn;
    private final Provider<Boolean> isFirstFinisher;
    private final Property<Integer> score;

    /** Default constructor for player */
    public Player(String nick,
                  Shelfie shelfie,
                  boolean isStartingPlayer,
                  boolean isConnected,
                  Function<Player, Provider<Boolean>> isCurrentTurnFactory,
                  Function<Player, Provider<Boolean>> isFirstFinisherFactory,
                  int score) {
        this.nick = nick;
        this.shelfie = shelfie;
        this.isStartingPlayer = isStartingPlayer;
        this.connected = new SerializableProperty<>(isConnected);
        this.isCurrentTurn = isCurrentTurnFactory.apply(this);
        this.isFirstFinisher = isFirstFinisherFactory.apply(this);
        this.score = new SerializableProperty<>(score);
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public Shelfie getShelfie() {
        return shelfie;
    }

    @Override
    public boolean isStartingPlayer() {
        return isStartingPlayer;
    }

    @Override
    public Property<Boolean> connected() {
        return connected;
    }

    @Override
    public Provider<Boolean> isCurrentTurn() {
        return isCurrentTurn;
    }

    @Override
    public Provider<Boolean> isFirstFinisher() {
        return isFirstFinisher;
    }

    @Override
    public Property<Integer> score() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Player player))
            return false;
        return nick.equals(player.nick) &&
                shelfie.equals(player.shelfie) &&
                isStartingPlayer == player.isStartingPlayer &&
                connected.get().equals(player.connected.get()) &&
                isCurrentTurn.get().equals(player.isCurrentTurn.get()) &&
                isFirstFinisher.get().equals(player.isFirstFinisher.get()) &&
                score.get().equals(player.score.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, shelfie, isStartingPlayer, connected.get(), isCurrentTurn.get(), isFirstFinisher.get(),
                score.get());
    }

    @Override
    public String toString() {
        return "Player{" +
                "nick='" + nick + '\'' +
                ", shelfie=" + shelfie +
                ", isStartingPlayer=" + isStartingPlayer +
                ", connected=" + connected +
                ", isCurrentTurn=" + isCurrentTurn +
                ", isFirstFinisher=" + isFirstFinisher +
                ", score=" + score +
                '}';
    }
}