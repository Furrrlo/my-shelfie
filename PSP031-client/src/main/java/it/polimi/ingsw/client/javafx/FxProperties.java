package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;

class FxProperties {

    private FxProperties() {
    }

    public static <T> ReadOnlyObjectProperty<T> toFxProperty(Provider<T> provider) {
        return toFxProperty("", null, provider);
    }

    public static <T> ReadOnlyObjectProperty<T> toFxProperty(String name,
                                                             @Nullable Object bean,
                                                             Provider<T> provider) {
        return new ReadOnlyObjectPropertyBase<>() {
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
        };
    }
}
