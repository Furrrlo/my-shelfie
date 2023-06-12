package it.polimi.ingsw.utils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Utilities for working with thread pools
 *
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.ScheduledExecutorService
 */
public class ThreadPools {

    private ThreadPools() {
    }

    /**
     * Returns a runnable which changes the name of the thread which runs it to the given one, executes the task,
     * then restores the previous name.
     * <p>
     * In essence, it changes the name of the running thread for the entire duration of the task
     * <p>
     * If the task throws an uncaught exception, the thread name will not be restored to the previous one
     * in order to be able to read it in the stack trace
     *
     * @param name new name of the thread to use for the duration of the task
     * @param task task to be executed
     * @return runnable which will execute the task with the changed thread name
     */
    public static Runnable giveNameToTask(String name, Runnable task) {
        return () -> {
            var th = Thread.currentThread();
            var prevName = th.getName();
            th.setName(name);
            task.run();
            // If there's an uncaught exception, keep the name
            th.setName(prevName);
        };
    }

    /**
     * Returns a runnable which changes the name of the thread which runs it to the given one, executes the task,
     * then restores the previous name.
     * <p>
     * In essence, it changes the name of the running thread for the entire duration of the task
     * <p>
     * If the task throws an uncaught exception, the thread name will not be restored to the previous one
     * in order to be able to read it in the stack trace
     *
     * @param name function to compute the new name of the thread to use for the duration of the task
     * @param task task to be executed
     * @return runnable which will execute the task with the changed thread name
     */
    public static Runnable giveNameToTask(Function<String, String> name, Runnable task) {
        return () -> {
            var th = Thread.currentThread();
            var prevName = th.getName();
            th.setName(name.apply(prevName));
            task.run();
            // If there's an uncaught exception, keep the name
            th.setName(prevName);
        };
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     * <p>
     * If a thread is interrupted during the call, it continues to block until the result is available,
     * and then re-interrupts the thread at the end.
     *
     * @return the computed result
     * @param <T> The result type returned by this Future's get method
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an exception
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> T getUninterruptibly(Future<T> future) throws ExecutionException {
        boolean wasInterrupted = false;
        try {
            while (true) {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
        } finally {
            if (wasInterrupted)
                Thread.currentThread().interrupt();
        }
    }
}
