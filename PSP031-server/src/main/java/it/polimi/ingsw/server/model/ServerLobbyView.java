package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayerView;
import it.polimi.ingsw.model.LobbyView;
import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public interface ServerLobbyView {

    int MIN_PLAYERS = LobbyView.MIN_PLAYERS;
    int MAX_PLAYERS = LobbyView.MAX_PLAYERS;

    /**
     * Returns the number of required players.
     * <p>
     * Can be:
     * - null: if the creator of the lobby hasn't set it yet (see {@link #isOpen()})
     * - 0: there's no number of required players (see {@link #hasRequiredPlayers()})
     * - a number between {@link LobbyView#MIN_PLAYERS} and {@link LobbyView#MAX_PLAYERS}
     * 
     * @return the number of required players
     */
    Provider<@Nullable Integer> requiredPlayers();

    /** Returns the list of joined players */
    Provider<? extends @Unmodifiable List<? extends LobbyPlayerView>> joinedPlayers();

    Provider<? extends @Nullable ServerGameAndController<? extends ServerGameView>> game();

    /**
     * Returns whether the specified nick is of the player which created the lobby or has inherited it
     * after the creator left
     * 
     * @param nick player's nick to check
     * @return true if the nick is of the player which created the lobby
     */
    default boolean isLobbyCreator(String nick) {
        return joinedPlayers().get().get(0).getNick().equals(nick);
    }

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
     * <p>
     * If this method returns false, the game can start when all players are ready
     * and {@link #MAX_PLAYERS} can join this lobby.
     * <p>
     * This method does NOT check how many players have joined
     *
     * @return true if requiredPlayers != 0
     */
    default boolean hasRequiredPlayers() {
        return isOpen() && !Objects.equals(requiredPlayers().get(), 0);
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

    /** Returns true if this lobby is open and there's space for another player */
    default boolean canOnePlayerJoin() {
        return isOpen() && !hasGameStarted() &&
                ((hasRequiredPlayers() && joinedPlayers().get().size() < requiredPlayers().get())
                        || (!hasRequiredPlayers() && joinedPlayers().get().size() < MAX_PLAYERS));
    }
}
