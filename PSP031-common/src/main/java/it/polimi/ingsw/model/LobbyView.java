package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface LobbyView extends Serializable {
    /**
     * @return number of required players
     */
    int getRequiredPlayers();

    /**
     * @return Property list of joined players
     */
    Provider<List<String>> joinedPlayers();

    Provider<? extends @Nullable GameAndController<?>> game();
}
