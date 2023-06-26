package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/** Modifiable implementation of {@link ServerLobbyView} */
public class ServerLobby implements ServerLobbyView {

    private final Property<@Nullable Integer> requiredPlayers;
    private final Property<List<LobbyPlayer>> joinedPlayers;
    private final Property<@Nullable ServerGameAndController<ServerGame>> game;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public ServerLobby() {
        this.requiredPlayers = SerializableProperty.nullableProperty(null);
        this.joinedPlayers = new SerializableProperty<>(List.of());
        this.game = SerializableProperty.nullableProperty(null);
    }

    @Override
    public Property<@Nullable Integer> requiredPlayers() {
        return requiredPlayers;
    }

    @Override
    public Property<@Unmodifiable List<LobbyPlayer>> joinedPlayers() {
        return joinedPlayers;
    }

    @Override
    public Property<@Nullable ServerGameAndController<ServerGame>> game() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerLobby that))
            return false;
        return Objects.equals(requiredPlayers.get(), that.requiredPlayers.get()) &&
                joinedPlayers.get().equals(that.joinedPlayers.get()) &&
                Objects.equals(game.get(), that.game.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredPlayers.get(), joinedPlayers.get(), game.get());
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