package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ServerCommonGoalView {

    Type getType();

    Provider<? extends @Unmodifiable List<? extends ServerPlayerView>> achieved();
}
