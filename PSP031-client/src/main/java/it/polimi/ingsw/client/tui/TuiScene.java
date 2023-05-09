package it.polimi.ingsw.client.tui;

public interface TuiScene {

    void render(TuiPrintStream out);

    default void close() {
    }
}
