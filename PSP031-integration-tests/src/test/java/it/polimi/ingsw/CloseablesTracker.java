package it.polimi.ingsw;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloseablesTracker implements Closeable {

    private final List<Closeable> tracked = new ArrayList<>();

    public <T extends Closeable> T register(T t) {
        tracked.add(t);
        return t;
    }

    @Override
    public void close() throws IOException {
        final var exs = new ArrayList<Throwable>();
        for (Closeable t : tracked) {
            try {
                t.close();
            } catch (Throwable th) {
                exs.add(th);
            }
        }

        if (!exs.isEmpty()) {
            var ex = new IOException("Failed to close all closeables");
            exs.forEach(ex::addSuppressed);
            throw ex;
        }
    }
}
