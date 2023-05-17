package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lobby implements LobbyView {

    private final Property<@Nullable Integer> requiredPlayers;
    private final Property<@Unmodifiable List<LobbyPlayer>> joinedPlayers;
    private final Property<@Nullable GameAndController<Game>> game;

    public Lobby(@Nullable Integer requiredPlayers, List<LobbyPlayer> joinedPlayers) {
        this(requiredPlayers, joinedPlayers, null);
    }

    public Lobby(@Nullable Integer requiredPlayers,
                 List<LobbyPlayer> joinedPlayers,
                 @Nullable GameAndController<Game> game) {

        this.requiredPlayers = SerializableProperty.nullableProperty(requiredPlayers);
        this.joinedPlayers = new SerializableProperty<>(List.copyOf(joinedPlayers));
        this.game = SerializableProperty.nullableProperty(game);
    }

    public void disconnectThePlayer(String nick) {
        var game = game().get();
        if (game == null) {
            joinedPlayers().update(players -> {
                List<LobbyPlayer> l = new ArrayList<>(players);
                l.removeIf(p -> p.getNick().equals(nick));
                return l;
            });
        } else {
            game.game().thePlayer().connected().set(false);
        }
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
    public Property<@Nullable GameAndController<Game>> game() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Lobby lobby))
            return false;
        return Objects.equals(requiredPlayers.get(), lobby.requiredPlayers.get()) &&
                joinedPlayers.get().equals(lobby.joinedPlayers.get()) &&
                Objects.equals(game.get(), lobby.game.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredPlayers.get(), joinedPlayers.get(), game.get());
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
