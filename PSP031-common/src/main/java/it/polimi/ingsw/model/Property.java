package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Property<T> extends Provider<T> {

    @SuppressWarnings("NullAway") // NullAway sadly doesn't implement generics properly yet
    static <T> void setNullable(Property<@Nullable T> prop, @Nullable T value) {
        prop.set(value);
    }

    void set(T val);

    default void update(Function<T, T> updater) {
        set(updater.apply(get()));
    }
}