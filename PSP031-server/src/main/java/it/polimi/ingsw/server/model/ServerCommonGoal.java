package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Type;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public class ServerCommonGoal implements ServerCommonGoalView {

    private final Type type;

    private final Property<@Unmodifiable List<ServerPlayer>> achieved;

    public ServerCommonGoal(Type type) {
        this.type = type;
        this.achieved = new SerializableProperty<>(List.of());
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Property<@Unmodifiable List<ServerPlayer>> achieved() {
        return achieved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerCommonGoal that))
            return false;
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