package it.polimi.ingsw.client.tui;

/**
 * Represents a scene to be rendered by a {@link TuiRenderer}
 * <p>
 * A scene represents an entire game screen, excluding the prompt. So a single scene can render the same content,
 * but have different prompts to allow the user to make different actions
 * 
 * @see TuiRenderer#setScene(TuiScene)
 */
public interface TuiScene {

    /**
     * Renders the current scene to the given output stream
     * 
     * @param out the output stream to render to
     */
    void render(TuiPrintStream out);

    /** Hook invoked by {@link TuiRenderer} when a scene is replaced by another one */
    default void close() {
    }
}
