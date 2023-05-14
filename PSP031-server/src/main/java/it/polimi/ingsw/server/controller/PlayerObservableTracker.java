package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.server.model.UserMessage;

import java.util.function.Consumer;

public interface PlayerObservableTracker {

    <T> Consumer<T> registerObserver(Provider<T> toObserve, ThrowingConsumer<T> observer);

    <T> void registerObserver(Property<UserMessage> message, ThrowingConsumer<T> updateMessage);

    interface ThrowingConsumer<T> {

        void accept(T t) throws DisconnectedException;
    }
}
