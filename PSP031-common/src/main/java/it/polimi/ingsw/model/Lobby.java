package it.polimi.ingsw.model;
import java.util.*;

/**
 * 
 */
public class Lobby implements LobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public Lobby( int requiredPlayers ){
        this.requiredPlayers = requiredPlayers;
        joinedPlayers = new PropertyImpl<List<String>>(new ArrayList<String>());
    }


    @Override
    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    @Override
    public Property<List<String>> joinedPlayers() {
        return joinedPlayers;
    }

}