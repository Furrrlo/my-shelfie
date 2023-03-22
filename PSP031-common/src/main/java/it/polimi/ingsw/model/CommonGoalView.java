package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.List;

public interface CommonGoalView extends Serializable {

    Type getType();

    Provider<? extends List<? extends PlayerView>> achieved();
}
