package it.polimi.ingsw.server.model;

import java.util.*;
import it.polimi.ingsw.model.*;

/**
 * 
 */
public class ServerLobby implements ServerLobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public ServerLobby( int requiredPlayers ){
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