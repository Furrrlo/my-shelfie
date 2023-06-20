package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.function.Consumer;

class FxProperties {

    private FxProperties() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObservableValue<?> compositeObservableValue(List<ObservableValue<?>> properties) {
        if (properties.size() == 0)
            return new SimpleObjectProperty<>();

        final var nullMarker = new Object();
        ObservableValue<?> currVal = ((ObservableValue) properties.get(0)).orElse(nullMarker);
        boolean first = true;
        for (ObservableValue<?> val : properties) {
            if (!first)
                currVal = currVal.flatMap(ignored -> ((ObservableValue) val).orElse(nullMarker));
            first = false;
        }
        return currVal;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObservableValue<?> compositeObservableValue(ObservableValue<?>... properties) {
        if (properties.length == 0)
            return new SimpleObjectProperty<>();

        final var nullMarker = new Object();
        ObservableValue<?> currVal = ((ObservableValue) properties[0]).orElse(nullMarker);
        boolean first = true;
        for (ObservableValue<?> val : properties) {
            if (!first)
                currVal = currVal.flatMap(ignored -> ((ObservableValue) val).orElse(nullMarker));
            first = false;
        }
        return currVal;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObservableValue<?> compositeObservableValue(List<ObservableValue<?>> properties,
                                                              ObservableValue<?>... additional) {
        if (properties.size() == 0 && additional.length == 0)
            return new SimpleObjectProperty<>();

        final var nullMarker = new Object();
        ObservableValue<?> currVal = ((ObservableValue) properties.get(0)).orElse(nullMarker);
        boolean first = true;
        for (ObservableValue<?> val : properties) {
            if (!first)
                currVal = currVal.flatMap(ignored -> ((ObservableValue) val).orElse(nullMarker));
            first = false;
        }
        for (ObservableValue<?> val : additional)
            currVal = currVal.flatMap(ignored -> ((ObservableValue) val).orElse(nullMarker));
        return currVal;
    }

    public static <T> ReadOnlyObjectProperty<T> toFxProperty(Provider<T> provider) {
        return toFxProperty("", null, provider);
    }

    public static <T> ReadOnlyObjectProperty<T> toFxProperty(String name,
                                                             @Nullable Object bean,
                                                             Provider<T> provider) {
        var fxProperty = new ReadOnlyObjectPropertyBase<T>() {

            private final Consumer<T> observer = v -> {
                // Make sure we run on the FX threads, as I don't think it supports concurrency
                if (Platform.isFxApplicationThread())
                    fireValueChangedEvent();
                else
                    Platform.runLater(this::fireValueChangedEvent);
            };

            @Override
            public @Nullable Object getBean() {
                return bean;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public T get() {
                return provider.get();
            }

            @Override
            protected void fireValueChangedEvent() {
                super.fireValueChangedEvent();
            }
        };
        // As long as the gui property lives, this will also live
        // When the gui component goes away, this will also automatically be removed
        provider.registerWeakObserver(fxProperty.observer);
        return fxProperty;
    }
}
