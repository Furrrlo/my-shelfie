package it.polimi.ingsw.server.model;


/**
 * 
 */
public class Player implements PlayerView {

    private String nick;
    private Shelfie shelfie;
    private PersonalGoal personalGoal;

    /**
     * Default constructor for player
     */
    public Player(String nick, PersonalGoal personalGoal) {
        this.nick = nick;
        shelfie = new Shelfie();
        this.personalGoal = personalGoal;
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