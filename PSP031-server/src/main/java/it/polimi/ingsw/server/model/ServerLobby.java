package it.polimi.ingsw.server.model;

import java.util.*;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;

public class ServerLobby implements ServerLobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public ServerLobby(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
        this.joinedPlayers = new PropertyImpl<>(new ArrayList<>());
    }

    @Override
    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    @Override
    public Property<List<String>> joinedPlayers() {
        return joinedPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerLobby that)) return false;
        return requiredPlayers == that.requiredPlayers &&
                joinedPlayers.equals(that.joinedPlayers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredPlayers, joinedPlayers);
    }

    @Override
    public String toString() {
        return "ServerLobby{" +
                "requiredPlayers=" + requiredPlayers +
                ", joinedPlayers=" + joinedPlayers +
                '}';
    }
}