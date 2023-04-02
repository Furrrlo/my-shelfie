package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Provider;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public abstract class BaseServerConnection implements PlayerObservableTracker, Closeable {

    protected final ServerController controller;
    private final ConcurrentMap<Provider<?>, Set<Consumer<?>>> observablesToObservers = new ConcurrentHashMap<>();

    protected final String nick;

    public BaseServerConnection(ServerController controller, String nick) {
        this.controller = controller;
        this.nick = nick;
    }

    public void disconnectPlayer(Throwable cause) {
        controller.runOnLocks(nick, () -> {
            unregisterObservers();
            controller.disconnectPlayer(nick, cause);
        });

        try {
            close();
        } catch (IOException e) {
            // TODO: log
            System.err.println("Failed to disconnect player " + nick);
            e.printStackTrace();
        }
    }

    @Override
    public <T> Consumer<T> registerObserver(Provider<T> toObserve, PlayerObservableTracker.ThrowingConsumer<T> observer) {
        return controller.supplyOnLocks(nick, () -> {
            final var observers = observablesToObservers
                    .computeIfAbsent(toObserve, v -> ConcurrentHashMap.newKeySet());
            Consumer<T> throwingObserver = t -> {
                try {
                    observer.accept(t);
                } catch (DisconnectedException e) {
                    disconnectPlayer(e);
                }
            };
            observers.add(throwingObserver);
            toObserve.registerObserver(throwingObserver);
            return throwingObserver;
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unregisterObservers() {
        controller.runOnLocks(nick, () -> observablesToObservers.forEach((provider, observers) -> observers
                .forEach(o -> ((Provider) provider).unregisterObserver(o))));
    }
}
