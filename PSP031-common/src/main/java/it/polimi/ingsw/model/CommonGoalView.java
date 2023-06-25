package it.polimi.ingsw.model;

import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;

public interface CommonGoalView extends Serializable {

    Type getType();

    /** Returns a provider of the list of players which achieved this goal, in order of time of achieving */
    Provider<? extends @Unmodifiable List<? extends PlayerView>> achieved();
}
