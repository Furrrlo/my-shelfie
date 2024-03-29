package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Modifiable implementation of {@link LobbyView} */
public class Lobby implements LobbyView {

    private final Property<@Nullable Integer> requiredPlayers;
    private final Property<@Unmodifiable List<LobbyPlayer>> joinedPlayers;
    private final Provider<Boolean> thePlayerConnected;
    private final Property<@Nullable GameAndController<Game>> game;

    public Lobby(@Nullable Integer requiredPlayers, List<LobbyPlayer> joinedPlayers, String thePlayerNick) {
        this(requiredPlayers, joinedPlayers, thePlayerNick, null);
    }

    public Lobby(@Nullable Integer requiredPlayers,
                 List<LobbyPlayer> joinedPlayers,
                 String thePlayerNick,
                 @Nullable GameAndController<Game> game) {

        this.requiredPlayers = SerializableProperty.nullableProperty(requiredPlayers);
        this.joinedPlayers = new SerializableProperty<>(List.copyOf(joinedPlayers));
        this.thePlayerConnected = joinedPlayers()
                .map(players -> players.stream().anyMatch(p -> p.getNick().equals(thePlayerNick)));
        this.game = SerializableProperty.nullableProperty(game);
    }

    public void disconnectThePlayer(String nick) {
        var game = game().get();
        if (game == null) {
            // Only trigger an observer update if necessary
            if (thePlayerConnected().get())
                joinedPlayers().update(players -> {
                    List<LobbyPlayer> l = new ArrayList<>(players);
                    l.removeIf(p -> p.getNick().equals(nick));
                    return l;
                });
        } else {
            // Only trigger an observer update if necessary
            if (game.game().thePlayer().connected().get())
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
    public Provider<Boolean> thePlayerConnected() {
        return thePlayerConnected;
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
