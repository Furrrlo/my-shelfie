package it.polimi.ingsw.model;

import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;

/**
 * Read-only interface of a common goal.
 * <p>
 * A common goal is defined as a goal which is shared by all players in the game
 * and can be achieved by anybody, where the score is handed out based on the
 * order of achieving.
 */
public interface CommonGoalView extends Serializable {

    /** Returns the type of this common goal */
    Type getType();

    /** Returns a provider of the list of players which achieved this goal, in order of time of achieving */
    Provider<? extends @Unmodifiable List<? extends PlayerView>> achieved();
}
