package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public abstract class BaseServerConnection implements PlayerObservableTracker, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServerConnection.class);

    protected final ServerController controller;
    private final ConcurrentMap<Provider<?>, Set<Consumer<?>>> observablesToObservers = new ConcurrentHashMap<>();

    protected final String nick;

    public BaseServerConnection(ServerController controller, String nick) {
        this.controller = controller;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void disconnectPlayer(Throwable cause) {
        LOGGER.error("Disconnecting {}", nick, cause);
        controller.runOnLocks(nick, () -> {
            unregisterObservers();
            controller.onDisconnectPlayer(nick, cause);
        });

        try {
            close();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect player {}", nick, e);
        }
    }

    @Override
    public abstract void close() throws IOException;

    public void onGameOver() {
        controller.runOnLocks(nick, this::unregisterObservers);

        try {
            doClosePlayerGame();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect player {}", nick, e);
        }
    }

    protected abstract void doClosePlayerGame() throws IOException;

    @Override
    public <T> Consumer<T> registerObserver(Provider<T> toObserve, ThrowingConsumer<T> observer) {
        return controller.supplyOnLocks(nick, () -> doRegisterObserver(toObserve, observer));
    }

    @Override
    public <T> Consumer<T> registerObserver(ServerController.LockBadge controllerLockBadge,
                                            Provider<T> toObserve,
                                            ThrowingConsumer<T> observer) {
        return doRegisterObserver(toObserve, observer);
    }

    private <T> Consumer<T> doRegisterObserver(Provider<T> toObserve, ThrowingConsumer<T> observer) {
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
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unregisterObservers() {
        controller.runOnLocks(nick, () -> {
            observablesToObservers.forEach((provider, observers) -> observers
                    .forEach(o -> ((Provider) provider).unregisterObserver(o)));
            observablesToObservers.clear();
        });
    }
}
