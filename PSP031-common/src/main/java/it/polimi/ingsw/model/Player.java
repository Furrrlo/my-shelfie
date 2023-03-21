package it.polimi.ingsw.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return nick.equals(player.nick) && shelfie.equals(player.shelfie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, shelfie);
    }

    @Override
    public String toString() {
        return "Player{" +
                "nick='" + nick + '\'' +
                ", shelfie=" + shelfie +
                '}';
    }
}