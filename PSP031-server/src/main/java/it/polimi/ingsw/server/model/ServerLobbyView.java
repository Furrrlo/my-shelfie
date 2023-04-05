package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayerView;
import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ServerLobbyView {

    /**
     * @return number of required players
     */
    int getRequiredPlayers();

    /**
     * @return Property list of joined players
     */
    Provider<? extends @Unmodifiable List<? extends LobbyPlayerView>> joinedPlayers();

    Provider<? extends @Nullable ServerGameAndController<? extends ServerGameView>> game();

    default boolean hasGameStarted() {
        return game().get() != null;
    }

    default boolean canOnePlayerJoin() {
        return !hasGameStarted() && joinedPlayers().get().size() < getRequiredPlayers();
    }
}
