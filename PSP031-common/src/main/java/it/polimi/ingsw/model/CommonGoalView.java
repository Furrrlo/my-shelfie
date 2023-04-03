package it.polimi.ingsw.model;

import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.List;

public interface CommonGoalView extends Serializable {

    Type getType();

    Provider<? extends @Unmodifiable List<? extends PlayerView>> achieved();
}
