package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Read-only interface of a common goal, as seen by the server.
 * <p>
 * This object does not differ from {@link it.polimi.ingsw.model.CommonGoalView}
 * other than the fact that it returns {@link ServerPlayerView} instead of
 * {@link it.polimi.ingsw.model.PlayerView} instances.
 *
 * @see it.polimi.ingsw.model.CommonGoalView
 */
public interface ServerCommonGoalView {

    /** Returns the type of this common goal */
    Type getType();

    /** Returns a provider of the list of players which achieved this goal, in order of time of achieving */
    Provider<? extends @Unmodifiable List<? extends ServerPlayerView>> achieved();
}
