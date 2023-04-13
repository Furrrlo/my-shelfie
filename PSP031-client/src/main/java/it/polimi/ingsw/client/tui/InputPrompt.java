package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Creates a free-input prompt
 * 
 * @see Prompt
 */
class InputPrompt extends BasePrompt {

    private final Action action;

    /**
     * Creates a free-input prompt
     *
     * @param text string to display
     * @param action method to validate the received input and, in case it's correct, execute actions
     */
    public InputPrompt(String text, Action action) {
        super(text);
        this.action = action;
    }

    @Override
    public Result handleChoice(TuiRenderer renderer, String input) {
        return action.run(renderer, actionCtx, input);
    }

    @Override
    public @Unmodifiable List<String> getChoices() {
        return List.of();
    }

    /** Method to validate the received input and, in case it's correct, execute actions */
    public interface Action {

        Result run(TuiRenderer renderer, ActionContext ctx, String input);
    }
}
