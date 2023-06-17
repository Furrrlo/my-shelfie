package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.NamedArg;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder factory implementation which allows us to provide additional values to be provided at construction time to
 * objects constructed by the {@link javafx.fxml.FXMLLoader} by using the {@link javafx.beans.NamedArg} mechanism
 */
class InjectingBuilderFactory implements BuilderFactory {

    private final BuilderFactory delegate;
    private final Map<String, Object> toInject = new HashMap<>();

    public InjectingBuilderFactory() {
        this(null);
    }

    public InjectingBuilderFactory(@Nullable BuilderFactory delegate) {
        if (delegate instanceof InjectingBuilderFactory other) {
            this.toInject.putAll(other.toInject);
            delegate = null;
        }

        this.delegate = delegate == null ? new JavaFXBuilderFactory() : delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Builder<?> getBuilder(Class<?> type) {
        var builder = delegate.getBuilder(type);
        if (builder instanceof Map<?, ?> && type.getPackageName().startsWith("it.polimi.ingsw"))
            ((Map<String, Object>) builder).putAll(toInject);
        return builder;
    }

    /**
     * Provide the value to be used when a constructed object uses a {@link javafx.beans.NamedArg} with the value
     * specified by {@code key}
     *
     * @param key the {@link NamedArg#value()}
     * @param value value to be provided when an object requests an arg named {@code key}
     */
    public void inject(String key, Object value) {
        toInject.put(key, value);
    }
}
