package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

public class ServerPlayer implements ServerPlayerView {

    private final String nick;
    private final Shelfie shelfie;
    private final PersonalGoal personalGoal;

    public ServerPlayer(String nick, PersonalGoal personalGoal) {
        this.nick = nick;
        this.personalGoal = personalGoal;
        this.shelfie = new Shelfie();
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
}