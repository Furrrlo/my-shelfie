package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

import java.util.Objects;

public class ServerPlayer implements ServerPlayerView {

    private final String nick;
    private final Shelfie shelfie;
    private final PersonalGoal personalGoal;
    private final Property<Boolean> connected;

    public ServerPlayer(String nick, PersonalGoal personalGoal) {
        this.nick = nick;
        this.personalGoal = personalGoal;
        this.shelfie = new Shelfie();
        this.connected = new SerializableProperty<>(true);
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
    public PersonalGoal getPersonalGoal() {
        return personalGoal;
    }

    @Override
    public Property<Boolean> connected() {
        return connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerPlayer that))
            return false;
        return nick.equals(that.nick) && shelfie.equals(that.shelfie) && personalGoal.equals(that.personalGoal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, shelfie, personalGoal);
    }

    @Override
    public String toString() {
        return "ServerPlayer{" +
                "nick='" + nick + '\'' +
                ", shelfie=" + shelfie +
                ", personalGoal=" + personalGoal +
                '}';
    }
}