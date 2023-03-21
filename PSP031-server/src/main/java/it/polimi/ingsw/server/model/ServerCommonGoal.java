package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class ServerCommonGoal implements ServerCommonGoalView {

    private final Type type;

    private final Property<List<ServerPlayer>> achieved;

    public ServerCommonGoal(Type type) {
        this.type = type;
        this.achieved = new PropertyImpl<>(new ArrayList<>());
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Property<List<ServerPlayer>> achieved() {
        return achieved;
    }
}