package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;

public interface LobbyView extends Serializable {

    int MIN_PLAYERS = 2;
    int MAX_PLAYERS = 4;

    /** Return number of required players */
    Provider<@Nullable Integer> requiredPlayers();

    /** Return Property list of joined players */
    Provider<? extends @Unmodifiable List<? extends LobbyPlayer>> joinedPlayers();

    Provider<? extends @Nullable GameAndController<?>> game();

    Provider<Boolean> thePlayerConnected();

    /**
     * Returns whether the specified nick is of the player which created the lobby or has inherited it
     * after the creator left
     *
     * @param nick player's nick to check
     * @return true if the nick is of the player which created the lobby
     */
    default boolean isLobbyCreator(String nick) {
        var joinedPlayers = joinedPlayers().get();
        return !joinedPlayers.isEmpty() && joinedPlayers.get(0).getNick().equals(nick);
    }
}
