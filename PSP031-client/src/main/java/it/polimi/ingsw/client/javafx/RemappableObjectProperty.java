package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

class RemappableObjectProperty<T> extends ReadOnlyObjectWrapper<T> {

    private static final Object NULL_MARKER = new Object();

    private final ObjectProperty<T> property;
    private @Nullable ObservableValue<? extends T> boundProperty;

    public RemappableObjectProperty(ObjectProperty<T> property) {
        bindBidirectional(this.property = property);
    }

    @Override
    public Object getBean() {
        return property.getBean();
    }

    @Override
    public String getName() {
        return property.getName();
    }

    /**
     * Returns an {@code ObservableValue} that holds the result of applying the
     * given mapping function on this value. The result is updated when this
     * {@code ObservableValue} changes. If this value is {@code null}, no
     * mapping is applied and the resulting value is also {@code null}.
     * </p>
     * Thi method is similar to the {@link #map(Function)} method, but it allows to
     * map a value and rebind it to the value itself, enabling the pattern:
     * <code>{@code
     * prop.bind(new SimpleStringProperty("abcd"));
     * prop.bind(prop.remap(String::toUpperCase));
     * }</code>
     *
     * @param <U> the type of values held by the resulting {@code ObservableValue}
     * @param mapper the mapping function to apply to a value, cannot be {@code null}
     * @return an {@code ObservableValue} that holds the result of applying the given
     *         mapping function on this value, or {@code null} when it
     *         is {@code null}; never returns {@code null}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> ObservableValue<U> remap(Function<? super @Nullable T, ? extends @Nullable U> mapper) {
        if (boundProperty != null)
            return boundProperty.map(mapper);
        return map(mapper);
    }

    /**
     * Returns an {@code ObservableValue} that holds the result of applying the
     * given mapping function on this value. The result is updated when this
     * {@code ObservableValue} changes. If this value is {@code null}, the
     * mapping is still applied.
     * </p>
     * Thi method is similar to the {@link #map(Function)} method, but it allows to
     * map a value and rebind it to the value itself, enabling the pattern:
     * <code>{@code
     * prop.bind(new SimpleStringProperty("abcd"));
     * prop.bind(prop.remap(String::toUpperCase));
     * }</code>
     *
     * @param <U> the type of values held by the resulting {@code ObservableValue}
     * @param mapper the mapping function to apply to a value, cannot be {@code null}
     * @return an {@code ObservableValue} that holds the result of applying the given
     *         mapping function on this value, or {@code null} when it
     *         is {@code null}; never returns {@code null}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    @SuppressWarnings({
            "NullAway", // Generics not supported
            "unchecked", // NULL_MARKER cast to T, won't be exposed externally anyway so not an issue
    })
    public <U> ObservableValue<U> remapNullable(Function<? super @Nullable T, ? extends @Nullable U> mapper) {
        if (boundProperty != null)
            return ((ObservableValue<T>) boundProperty)
                    .orElse((T) NULL_MARKER)
                    .map(v -> mapper.apply(v == NULL_MARKER ? null : v));
        return orElse((T) NULL_MARKER).map(v -> mapper.apply(v == NULL_MARKER ? null : v));
    }

    @Override
    public void bind(ObservableValue<? extends T> newObservable) {
        super.bind(newObservable);
        boundProperty = newObservable;
    }

    @Override
    public void unbind() {
        super.unbind();
        boundProperty = null;
    }
}
