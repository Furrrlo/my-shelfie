package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

/**
 * 
 */
public class ServerPlayer implements ServerPlayerView {

    private String nick;
    private Shelfie shelfie;
    private PersonalGoal personalGoal;

    /**
     * Default constructor for player
     */
    public ServerPlayer(String nick, PersonalGoal personalGoal) {
        this.nick = nick;
        this.personalGoal=personalGoal;
        shelfie = new Shelfie();
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