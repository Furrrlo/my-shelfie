package it.polimi.ingsw.utils;

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
}
