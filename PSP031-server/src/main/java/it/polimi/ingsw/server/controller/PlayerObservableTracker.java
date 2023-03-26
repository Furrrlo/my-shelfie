package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.Provider;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

class PlayerObservableTracker {

    private final ConcurrentMap<String, ConcurrentMap<Provider<?>, Set<Consumer<?>>>> nicksToObservables = new ConcurrentHashMap<>();

    public <T> void registerObserverFor(String nick, Provider<T> toObserve, Consumer<T> observer) {
        final var playerObservers = nicksToObservables
                .computeIfAbsent(nick, v -> new ConcurrentHashMap<>())
                .computeIfAbsent(toObserve, v -> ConcurrentHashMap.newKeySet());
        playerObservers.add(observer);
        toObserve.registerObserver(observer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void unregisterObserversFor(String nick) {
        final var observables = nicksToObservables.remove(nick);
        if (observables != null)
            observables.forEach((provider, observers) -> observers
                    .forEach(o -> ((Provider) provider).unregisterObserver(o)));
    }
}
