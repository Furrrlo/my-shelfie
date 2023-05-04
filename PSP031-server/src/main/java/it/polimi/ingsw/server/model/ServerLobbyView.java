package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayerView;
import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ServerLobbyView {

    int MIN_PLAYERS = 2;
    int MAX_PLAYERS = 4;

    /** Returns the number of required players */
    Provider<@Nullable Integer> requiredPlayers();

    /** Returns the list of joined players */
    Provider<? extends @Unmodifiable List<? extends LobbyPlayerView>> joinedPlayers();

    Provider<? extends @Nullable ServerGameAndController<? extends ServerGameView>> game();

    /**
     * Return whether the creator of this lobby has set {@link #requiredPlayers()}.
     * If this method returns false, nobody can join this lobby yet.
     * This method does NOT check how many players have joined
     *
     * @return true if requiredPlayers != null
     */
    default boolean isOpen() {
        return requiredPlayers().get() != null;
    }

    /**
     * Return whether {@link #requiredPlayers()} is set
     * If this method returns false, the game can start when all players are ready
     * and {@link #MAX_PLAYERS} can join this lobby.
     * This method does NOT check how many players have joined
     *
     * @return true if requiredPlayers != 0
     */
    default boolean hasRequiredPlayers() {
        return isOpen() && requiredPlayers().get() != 0;
    }

    /**
     * Returns whether the game has been started, so that it can be safely be grabbed using
     * the {@link #game()} method.
     *
     * @return true if the game has been started
     */
    default boolean hasGameStarted() {
        return game().get() != null;
    }

    /** Returns true if there's space for another player in this lobby */
    default boolean canOnePlayerJoin() {
        return !hasGameStarted() &&
                ((hasRequiredPlayers() && joinedPlayers().get().size() < requiredPlayers().get())
                        || (!hasRequiredPlayers() && joinedPlayers().get().size() < MAX_PLAYERS));
    }
}
