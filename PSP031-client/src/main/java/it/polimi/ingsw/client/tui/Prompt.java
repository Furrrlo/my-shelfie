package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Textual prompt which can be displayed to the player, which can then answer it.
 * <p>
 * This is the main way the TUI can request input from the player.
 * 
 * @see TuiRenderer#setPrompt(Prompt)
 */
public interface Prompt {

    /** Returns the parent prompt of this prompt, or null if this is a root prompt */
    @Nullable
    Prompt getParent();

    /** Returns the request text of this prompt to show to the user */
    String getText();

    /** Returns indications about the possible options (if any) the user can choose */
    @Unmodifiable
    List<String> getChoices();

    /**
     * Tries to solve/advance this prompt with the given input
     * 
     * @param renderer renderer where this prompt is being invoked onto
     * @param input user-provided input to answer this prompt
     * @return a result type based on whether the provided user input has satisfied the request,
     *         needs further info with other prompts, or was invalid
     * @see ActionContext
     */
    Result handleChoice(TuiRenderer renderer, String input);

    /** Result of the invocation of {@link #handleChoice(TuiRenderer, String)} */
    interface Result {

        /** Returns the type of result */
        ResultType type();
    }

    /**
     * Type of possible results returned by {@link #handleChoice(TuiRenderer, String)}
     * 
     * @see ActionContext
     */
    enum ResultType {
        PROMPT,
        SUBPROMPT,
        INVALID,
        DONE
    }

    /**
     * Result type which has an optional error message
     *
     * Only results of type {@link ResultType#INVALID}, {@link ResultType#PROMPT}
     * and {@link ResultType#SUBPROMPT} can be safely casted to this type.
     */
    interface InvalidResult extends Result {

        /** Returns the error message, if present */
        @Nullable
        String errorMsg();
    }

    /**
     * Result type which has a newly attached prompt and optionally an error message
     *
     * Only results of type {@link ResultType#PROMPT} and {@link ResultType#SUBPROMPT}
     * can be safely cast to this type.
     */
    interface PromptResult extends InvalidResult {

        /** Returns the newly-created prompt */
        Prompt prompt();
    }

    /**
     * Context object which is passed to Prompt actions to aid them in constructing
     * and returning a valid result type.
     * 
     * @see ChoicePrompt.Action
     * @see InputPrompt.Action
     */
    interface ActionContext {

        /** Returns a factory which can be used to create sub-prompts */
        Factory subPrompt();

        /** Returns a factory which can be used to create root prompts */
        Factory rootPrompt();

        /** Returns a result signaling that the provided input satisfied the prompt */
        Result done();

        /**
         * Returns a result signaling that the provided input satisfied the prompt,
         * but further info is needed with another prompt
         *
         * @param prompt prompt used to request further info
         * @return the result described above
         */
        Result prompt(Prompt prompt);

        /**
         * Returns a result signaling that the provided input did not satisfy the prompt,
         * and further info will be requested with a different prompt
         *
         * @param errorMsg error message to show to the user which explains why the input was invalid
         * @param prompt prompt used to request further info
         * @return the result described above
         */
        Result prompt(String errorMsg, Prompt prompt);

        /**
         * Returns a result signaling that the provided input did not satisfy the prompt
         *
         * @param errorMsg error message to show to the user which explains why the input was invalid
         * @return a result signaling that the provided input did not satisfy the prompt
         */
        Result invalid(String errorMsg);
    }

    /** Factory which creates prompts */
    interface Factory {

        /**
         * Creates a multiple-choice prompt
         *
         * @param text string to display
         * @param choices options that the user can select, with their respective actions
         * @return a newly created multiple-choice prompt
         * @see ChoicePrompt
         */
        Prompt choice(String text, ChoicePrompt.Choice... choices);

        /**
         * Creates a free-input prompt
         *
         * @param text string to display
         * @param action method to validate the received input and, in case it's correct, execute actions
         * @return a newly created free-input prompt
         * @see InputPrompt
         */
        Prompt input(String text, InputPrompt.Action action);
    }

    /** Prompt factory which creates prompts without any parent (so-called "root" prompts) */
    Factory ROOT_PROMPT_FACTORY = new Factory() {

        @Override
        public Prompt choice(String text, ChoicePrompt.Choice... choices) {
            return new ChoicePrompt(text, choices);
        }

        @Override
        public Prompt input(String text, InputPrompt.Action action) {
            return new InputPrompt(text, action);
        }
    };
}
