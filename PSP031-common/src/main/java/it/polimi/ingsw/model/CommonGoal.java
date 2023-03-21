package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class CommonGoal implements CommonGoalView {

    private final Type type;

    private final Property<List<Player>> achieved;

    public CommonGoal(Type type) {
        this.type = type;
        this.achieved = new PropertyImpl<>(new ArrayList<>());
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Property<List<Player>> achieved() {
        return achieved;
    }
}