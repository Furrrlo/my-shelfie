package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ServerLobbyView {

    /**
     * @return number of required players
     */
    int getRequiredPlayers();

    /**
     * @return Property list of joined players
     */
    Provider<List<String>> joinedPlayers();

    Provider<? extends @Nullable ServerGameView> game();
}
