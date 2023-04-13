package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.ConsoleColors;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

class TuiRenderer implements Closeable {

    private final BlockingQueue<Event> events = new LinkedBlockingQueue<>();

    private final Deque<Prompt> promptStack;
    private @Nullable Consumer<TuiPrintStream> scene;

    private final Thread renderThread;
    private final Thread inputThread;

    /**
     * Creates a new TUI renderer which uses the given streams to perform
     * IO operations.
     * 
     * @param outputStream stream to print to
     * @param inputStream stream to read user input from
     * @param initialPrompt initial prompt to ask the user
     * @param scene initial scene to be rendered
     */
    public TuiRenderer(OutputStream outputStream,
                       InputStream inputStream,
                       Prompt initialPrompt,
                       @Nullable Consumer<TuiPrintStream> scene) {
        this.promptStack = new ArrayDeque<>(List.of(initialPrompt));
        this.scene = scene;

        this.renderThread = new Thread(() -> renderLoop(outputStream));
        this.renderThread.setName(this + "-render-thread");
        this.renderThread.start();

        this.inputThread = new Thread(() -> inputLoop(inputStream));
        this.inputThread.setName(this + "-input-thread");
        this.inputThread.start();

        rerender();
    }

    private void renderLoop(OutputStream os) {
        final TuiPrintStream out = new TuiPrintStream(os, System.console() != null
                ? System.console().charset()
                : Charset.defaultCharset());
        try {
            do {
                Event evt = events.take();
                Prompt currPrompt = promptStack.peek();

                String errorMsg = "";
                if (evt instanceof SetSceneEvent sceneEvent) {
                    scene = sceneEvent.scene();
                } else if (evt instanceof SetPromptEvent promptEvent) {
                    // Replace the current prompt with the new one
                    promptStack.clear();
                    promptStack.push(currPrompt = promptEvent.prompt());
                } else if (evt instanceof InputEvent input) {
                    Prompt.Result res;
                    if (input.in().equalsIgnoreCase("B") &&
                            currPrompt != null &&
                            currPrompt.getParent() != null) {
                        // Back, pop the last
                        promptStack.pop();
                        currPrompt = promptStack.peek();
                    } else if (currPrompt == null) {
                        // Not back, not a valid number, no prompt or not a valid choice
                        errorMsg = "Invalid input " + input.in();
                    } else if ((res = currPrompt.handleChoice(this, input.in())).type() == ChoicePrompt.ResultType.INVALID) {
                        // Invalid choice
                        var invalidResult = (Prompt.InvalidResult) res;
                        errorMsg = invalidResult.errorMsg() == null ? "Invalid input " + input.in() : invalidResult.errorMsg();
                    } else if (res.type() == ChoicePrompt.ResultType.PROMPT) {
                        // Replace the current prompt with the new one
                        var promptResult = (Prompt.PromptResult) res;
                        promptStack.clear();
                        promptStack.push(currPrompt = promptResult.prompt());
                        errorMsg = promptResult.errorMsg() == null ? errorMsg : promptResult.errorMsg();
                    } else if (res.type() == ChoicePrompt.ResultType.SUBPROMPT) {
                        // Add a new subprompt
                        var promptResult = (Prompt.PromptResult) res;
                        promptStack.push(currPrompt = promptResult.prompt());
                        errorMsg = promptResult.errorMsg() == null ? errorMsg : promptResult.errorMsg();
                    } else /* if(res.type() == Prompt.ResultType.DONE) */ {
                        // Delete all until we get to the parent
                        while (promptStack.size() > 1)
                            promptStack.pop();
                        currPrompt = promptStack.peek();
                    }
                }

                out.cursor(1, 1);
                out.eraseInDisplay();

                if (scene != null)
                    scene.accept(out);

                out.println(ConsoleColors.RED_BOLD_BRIGHT + errorMsg + ConsoleColors.RESET);
                if (currPrompt != null) {
                    out.println(currPrompt.getText());

                    if (currPrompt.getParent() != null)
                        out.println("(type B to go back)");

                    final var currPrompt0 = currPrompt;
                    IntStream.range(0, currPrompt.getChoices().size())
                            .forEach(idx -> out.println((idx + 1) + ". " + currPrompt0.getChoices().get(idx)));
                }

                out.flush();
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedException ex) {
            // We got closed
        } catch (Throwable t) {
            // TODO: log
            System.err.println("Uncaught exception in TuiRenderer render thread");
            t.printStackTrace();
        }
    }

    private void inputLoop(InputStream is) {
        final var sc = new Scanner(is, System.console() != null
                ? System.console().charset()
                : Charset.defaultCharset());
        try {
            do {
                events.add(new InputEvent(sc.nextLine())); // TODO: how do we interrupt this?
            } while (!Thread.currentThread().isInterrupted());
        } catch (Throwable t) {
            // TODO: log
            System.err.println("Uncaught exception in TuiRenderer render thread");
            t.printStackTrace();
        }
    }

    @Override
    public void close() {
        renderThread.interrupt();
        inputThread.interrupt();
    }

    /** Request that the TUI is re-rendered sometime in the future */
    public void rerender() {
        events.add(RenderEvent.INSTANCE);
    }

    /**
     * Set the current scene to be rendered, replacing any already existing one
     *
     * There can be only 1 scene active at any time
     *
     * @param scene scene to render
     */
    public void setScene(Consumer<TuiPrintStream> scene) {
        this.scene = scene;
        rerender();
    }

    /**
     * Set the current root prompt to be rendered and interacted by the user,
     * replacing any already existing one.
     *
     * There can be only 1 root prompt active at any time, eventually with its children
     * (if any exists/are required)
     *
     * @param prompt root prompt to be rendered and interacted by the user
     */
    public void setPrompt(Prompt prompt) {
        events.add(new SetPromptEvent(prompt));
    }

    private sealed interface Event permits TuiRenderer.InputEvent, SetSceneEvent, SetPromptEvent, RenderEvent {
    }

    private record InputEvent(String in) implements Event {
    }

    private record SetSceneEvent(@Nullable Consumer<TuiPrintStream> scene) implements Event {
    }

    private record SetPromptEvent(Prompt prompt) implements Event {
    }

    private record RenderEvent() implements Event {
        static RenderEvent INSTANCE = new RenderEvent();
    }
}
