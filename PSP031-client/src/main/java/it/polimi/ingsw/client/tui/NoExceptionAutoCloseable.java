package it.polimi.ingsw.client.tui;

public interface NoExceptionAutoCloseable extends AutoCloseable {

    @Override
    void close();
}
