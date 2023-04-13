package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Base prompt which implements the logic common to all types of prompts,
 * such as creating sub-prompts
 */
abstract class BasePrompt implements Prompt {

    private @Nullable Prompt parent;
    private final String text;

    public BasePrompt(String text) {
        this.text = text;
    }

    private void doSetSelfAsParent(BasePrompt prompt) {
        prompt.parent = BasePrompt.this;
    }

    protected final Factory subPromptFactory = new Factory() {

        @Override
        public Prompt choice(String text, ChoicePrompt.Choice... choices) {
            var prompt = new ChoicePrompt(text, choices);
            doSetSelfAsParent(prompt);
            return prompt;
        }

        @Override
        public Prompt input(String text, InputPrompt.Action action) {
            var prompt = new InputPrompt(text, action);
            doSetSelfAsParent(prompt);
            return prompt;
        }
    };

    protected final ActionContext actionCtx = new ActionContext() {

        @Override
        public Factory subPrompt() {
            return subPromptFactory;
        }

        @Override
        public Factory rootPrompt() {
            return ROOT_PROMPT_FACTORY;
        }

        @Override
        public Result done() {
            return new ResultImpl(ResultType.DONE, null, null);
        }

        @Override
        public Result prompt(Prompt child) {
            if (child.getParent() == null)
                return new ResultImpl(ResultType.PROMPT, null, child);
            if (child.getParent() == BasePrompt.this)
                return new ResultImpl(ResultType.SUBPROMPT, null, child);

            throw new IllegalStateException(String.format(
                    "Subprompt parent can only be this instance (prompt: %s, parent: %s)",
                    BasePrompt.this, child.getParent()));
        }

        @Override
        public Result prompt(String errorMsg, Prompt child) {
            if (child.getParent() == null)
                return new ResultImpl(ResultType.PROMPT, errorMsg, child);
            if (child.getParent() == BasePrompt.this)
                return new ResultImpl(ResultType.SUBPROMPT, errorMsg, child);

            throw new IllegalStateException(String.format(
                    "Subprompt parent can only be this instance (prompt: %s, parent: %s)",
                    BasePrompt.this, child.getParent()));
        }

        @Override
        public Result invalid(String errorMsg) {
            return new ResultImpl(ResultType.INVALID, errorMsg, null);
        }
    };

    @Override
    public @Nullable Prompt getParent() {
        return parent;
    }

    @Override
    public String getText() {
        return text;
    }

    protected record ResultImpl(
            ResultType type,
            @Nullable String errorMsg,
            @Nullable Prompt prompt) implements Result, InvalidResult, PromptResult {

        @Override
        public @Nullable String errorMsg() {
            if (type != ResultType.INVALID && type != ResultType.PROMPT && type != ResultType.SUBPROMPT)
                throw new IllegalStateException("Not an invalid result");
            return errorMsg;
        }

        @Override
        public Prompt prompt() {
            if (type != ResultType.PROMPT && type != ResultType.SUBPROMPT)
                throw new IllegalStateException("Not a prompt/subprompt result");
            return Objects.requireNonNull(prompt, "Prompt cannot be null");
        }
    }
}
