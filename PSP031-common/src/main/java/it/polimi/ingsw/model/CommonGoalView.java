package it.polimi.ingsw.model;

import java.util.List;

public interface CommonGoalView {

    /**
     * @return
     */
    Type getType();

    /**
     * @return
     */
    Provider<? extends List<? extends PlayerView>> achieved();
}
