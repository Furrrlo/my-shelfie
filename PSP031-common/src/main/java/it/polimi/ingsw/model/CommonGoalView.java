package it.polimi.ingsw.model;

import java.util.List;

public interface CommonGoalView {

    Type getType();

    Provider<? extends List<? extends PlayerView>> achieved();
}
