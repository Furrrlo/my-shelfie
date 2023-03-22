package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lobby implements LobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;
    private final Property<@Nullable GameAndController<Game>> game;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public Lobby(int requiredPlayers) {
        this(requiredPlayers, new ArrayList<>());
    }

    public Lobby(int requiredPlayers, List<String> joinedPlayers) {
        this(requiredPlayers, joinedPlayers, null);
    }

    public Lobby(int requiredPlayers,
                 List<String> joinedPlayers,
                 @Nullable GameAndController<Game> game) {

        this.requiredPlayers = requiredPlayers;
        this.joinedPlayers = new SerializableProperty<>(joinedPlayers);
        this.game = SerializableProperty.nullableProperty(game);
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
    public Property<@Nullable GameAndController<Game>> game() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lobby lobby)) return false;
        return requiredPlayers == lobby.requiredPlayers &&
                joinedPlayers.equals(lobby.joinedPlayers) &&
                game.equals(lobby.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredPlayers, joinedPlayers, game);
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "requiredPlayers=" + requiredPlayers +
                ", joinedPlayers=" + joinedPlayers +
                ", game=" + game +
                '}';
    }
}
