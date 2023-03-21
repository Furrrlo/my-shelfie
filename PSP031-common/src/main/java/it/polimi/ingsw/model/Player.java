package it.polimi.ingsw.model;

public class Player implements PlayerView {

    private final String nick;
    private final Shelfie shelfie;

    /**
     * Default constructor for player
     */
    public Player(String nick) {
        this.nick = nick;
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
}