package it.polimi.ingsw.model;

/**
 * 
 */
public class Player implements PlayerView {

    private String nick;
    private Shelfie shelfie;

    /**
     * Default constructor for player
     */
    public Player(String nick, PersonalGoal personalGoal) {
        this.nick = nick;
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

}