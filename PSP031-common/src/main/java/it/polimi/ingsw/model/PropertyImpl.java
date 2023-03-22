package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class PropertyImpl<T> implements Property<T>, Serializable {

    private T val;

    private final transient Set<Consumer<? super T>> observers = new LinkedHashSet<>();

    @SuppressWarnings("NullAway") // NullAway sadly doesn't implement generics properly yet
    public static <T> Property<@Nullable T> nullableProperty(@Nullable T prop) {
        return new PropertyImpl<>(prop);
    }

    /**
     * Default constructor
     */
    public PropertyImpl(T val) {
        this.val = val;
    }

    /**
     * Custom serialization hook defined by Java spec.
     * <p>
     * We use it to create a PropertyImpl with a non-null observers list,
     * as by default serialization leaves final fields as nulls.
     */
    @Serial
    private Object readResolve() throws ObjectStreamException {
        return new PropertyImpl<>(val);
    }

    @Override
    public T get() {
        return val;
    }

    @Override
    public void set(T val) {
        this.val = val;
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        observers.add(o);
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        observers.remove(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyImpl<?> that)) return false;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public String toString() {
        return "PropertyImpl{" +
                "value=" + val +
                ", observers=" + observers +
                '}';
    }
}