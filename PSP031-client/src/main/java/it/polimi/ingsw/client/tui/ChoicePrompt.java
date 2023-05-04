package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Multiple-choice prompt
 * 
 * @see Prompt
 */
class ChoicePrompt extends BasePrompt {

    private final @Unmodifiable List<Choice> choices;

    /**
     * Creates a multiple-choice prompt
     *
     * @param text string to display
     * @param choices options that the user can select, with their respective actions
     */
    public ChoicePrompt(String text, Choice... choices) {
        super(text);
        this.choices = List.of(choices);
    }

    @Override
    public Result handleChoice(TuiRenderer renderer, String input) {
        final int num;
        try {
            num = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return new ResultImpl(ResultType.INVALID, "Invalid choice " + input, null);
        }

        if (num < 1 || num > choices.size())
            return new ResultImpl(ResultType.INVALID, "Invalid choice " + num, null);

        return choices.stream()
                .filter(c -> c.visible.get())
                .toList()
                .get(num - 1)
                .action()
                .run(renderer, actionCtx);
    }

    @Override
    public @Unmodifiable List<String> getChoices() {
        return choices.stream()
                .filter(c -> c.visible.get())
                .map(Choice::text)
                .collect(Collectors.toList());
    }

    /**
     * Choice of a multiple choice prompt
     *
     * @param text string which describes the choice
     * @param action action to run when the choice is chosen by the user
     * @param visible supplier that allows you to hide or show this choice
     */
    public record Choice(String text, Action action, Supplier<Boolean> visible) {
        public Choice(String text, Action action) {
            this(text, action, () -> true);
        }
    }

    /** Action to run when a choice is chosen by the user */
    public interface Action {

        Result run(TuiRenderer renderer, ActionContext ctx);
    }
}
