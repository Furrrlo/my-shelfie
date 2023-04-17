package it.polimi.ingsw.utils;

import java.util.function.Function;

public class ThreadPools {

    private ThreadPools() {
    }

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
