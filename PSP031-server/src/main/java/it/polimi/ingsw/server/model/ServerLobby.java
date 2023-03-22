package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerLobby implements ServerLobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;
    private final Property<@Nullable ServerGame> game;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public ServerLobby(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
        this.joinedPlayers = new SerializableProperty<>(new ArrayList<>());
        this.game = SerializableProperty.nullableProperty(null);
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
    public Property<@Nullable ServerGame> game() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerLobby that))
            return false;
        return requiredPlayers == that.requiredPlayers &&
                joinedPlayers.equals(that.joinedPlayers) &&
                game.equals(that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredPlayers, joinedPlayers, game);
    }

    @Override
    public String toString() {
        return "ServerLobby{" +
                "requiredPlayers=" + requiredPlayers +
                ", joinedPlayers=" + joinedPlayers +
                ", game=" + game +
                '}';
    }
}