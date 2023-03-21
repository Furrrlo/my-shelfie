package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerCommonGoal that)) return false;
        return type == that.type && achieved.equals(that.achieved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, achieved);
    }

    @Override
    public String toString() {
        return "ServerCommonGoal{" +
                "type=" + type +
                ", achieved=" + achieved +
                '}';
    }
}