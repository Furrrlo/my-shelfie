package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ServerCommonGoalView {

    Type getType();

    /** Returns a provider of the list of players which achieved this goal, in order of time of achieving */
    Provider<? extends @Unmodifiable List<? extends ServerPlayerView>> achieved();
}
