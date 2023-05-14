package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Provider;

import java.util.function.Consumer;

public interface PlayerObservableTracker {

    <T> Consumer<T> registerObserver(Provider<T> toObserve, ThrowingConsumer<T> observer);

    interface ThrowingConsumer<T> {

        void accept(T t) throws DisconnectedException;
    }
}
