package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Provider;

import java.util.function.Consumer;

/**
 * Allows to track observables registered by a player in order to automatically
 * unregister them once said player disconnects
 *
 * @see Provider#registerObserver(Consumer)
 * @see Provider#unregisterObserver(Consumer)
 */
public interface PlayerObservableTracker {

    /**
     * Register an observable for the given player and track it in order to remove
     * it on disconnection
     * <p>
     * In addition, the observer will be wrapped to catch any potential disconnection
     * exceptions and act based on those to close the connection and signal the
     * player as disconnected
     *
     * @param toObserve value the current player is interested in observing
     * @param observer player's observable
     * @return the actual observer registered to the value, which is an implementation that wraps
     *         the given {@code toObserve} parameter
     * @param <T> type of the observable value
     * @see #registerObserver(ServerController.LockBadge, Provider, ThrowingConsumer)
     */
    <T> Consumer<T> registerObserver(Provider<T> toObserve, ThrowingConsumer<T> observer);

    /**
     * Register an observable for the given player and track it in order to remove
     * it on disconnection
     * <p>
     * In addition, the observer will be wrapped to catch any potential disconnection
     * exceptions and act based on those to close the connection and signal the
     * player as disconnected
     * <p>
     * This method, in contrast to its overload, requires a badge to only allow the ServerController
     * to call it. By doing so, it can skip any locking as it assumes the caller already handled it.
     * This also allows skipping code that searches for the correct lock given a player's nick
     *
     * @param toObserve value the current player is interested in observing
     * @param observer player's observable
     * @return the actual observer registered to the value, which is an implementation that wraps
     *         the given {@code toObserve} parameter
     * @param <T> type of the observable value
     * @see it.polimi.ingsw.server.controller.ServerController.LockBadge
     * @see #registerObserver(Provider, ThrowingConsumer)
     */
    <T> Consumer<T> registerObserver(ServerController.LockBadge controllerLockBadge,
                                     Provider<T> toObserve,
                                     ThrowingConsumer<T> observer);

    /**
     * Consumer which is allowed to throw disconnection related user exceptions, pushing the burden
     * of handling them on someone else
     *
     * @param <T> type of value to consume
     */
    interface ThrowingConsumer<T> {

        /**
         * Performs this operation on the given argument.
         * 
         * @param t the input argument
         */
        void accept(T t) throws DisconnectedException;
    }
}
