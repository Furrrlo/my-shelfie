package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface LobbyView extends Serializable {
    /** Return number of required players */
    int getRequiredPlayers();

    /** Return Property list of joined players */
    Provider<? extends List<? extends LobbyPlayer>> joinedPlayers();

    Provider<? extends @Nullable GameAndController<?>> game();
}
