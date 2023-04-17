package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayerView;
import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ServerLobbyView {

    /** Returns the number of required players */
    int getRequiredPlayers();

    /** Returns the list of joined players */
    Provider<? extends @Unmodifiable List<? extends LobbyPlayerView>> joinedPlayers();

    Provider<? extends @Nullable ServerGameAndController<? extends ServerGameView>> game();

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
        return !hasGameStarted() && joinedPlayers().get().size() < getRequiredPlayers();
    }
}
