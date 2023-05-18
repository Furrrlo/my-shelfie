package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

class TuiRenderer implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TuiRenderer.class);
    private static final int RESIZE_PER_SECOND = 30;

    private final BlockingQueue<Event> events = new LinkedBlockingQueue<>();

    private final Deque<Prompt> promptStack;
    private @Nullable TuiScene scene;

    private final Thread renderThread;
    private final Thread inputThread;
    private final Thread resizeThread;

    /**
     * Creates a new TUI renderer which uses the given streams to perform
     * IO operations.
     *
     * @param outputStream stream to print to
     * @param inputReader stream to read user input from
     * @param initialPrompt initial prompt to ask the user
     * @param scene initial scene to be rendered
     */
    public TuiRenderer(TuiPrintStream outputStream,
                       Reader inputReader,
                       Prompt initialPrompt,
                       @Nullable TuiScene scene) {
        this.promptStack = new ArrayDeque<>(List.of(initialPrompt));
        this.scene = scene;

        this.renderThread = new Thread(() -> renderLoop(outputStream));
        this.renderThread.setName(this + "-render-thread");
        this.renderThread.start();

        this.inputThread = new Thread(() -> inputLoop(inputReader));
        this.inputThread.setName(this + "-input-thread");
        this.inputThread.start();

        this.resizeThread = new Thread(() -> resizeLoop(outputStream));
        this.resizeThread.setName(this + "-input-thread");
        this.resizeThread.start();

        rerender();
    }

    private void renderLoop(TuiPrintStream out) {
        try {
            String errorMsg = "";
            do {
                Event evt = events.take();
                Prompt currPrompt = promptStack.peek();

                switch (evt) {
                    case RenderEvent ignored -> {
                        // Coalesce render events into a single re-render
                        while (events.peek() instanceof RenderEvent)
                            events.remove();
                    }
                    case SetSceneEvent sceneEvent -> {
                        if (scene != null)
                            scene.close();
                        scene = sceneEvent.scene();
                    }
                    case SetPromptEvent promptEvent -> {
                        // Replace the current prompt with the new one
                        promptStack.clear();
                        promptStack.push(currPrompt = promptEvent.prompt());
                        errorMsg = "";
                    }
                    case InputEvent input -> {
                        Prompt.Result res;
                        if (input.in().equalsIgnoreCase("B") &&
                                currPrompt != null &&
                                currPrompt.getParent() != null) {
                            // Back, pop the last
                            promptStack.pop();
                            currPrompt = promptStack.peek();
                            errorMsg = "";
                        } else if (currPrompt == null) {
                            // Not back, not a valid number, no prompt or not a valid choice
                            errorMsg = "Invalid input " + input.in();
                        } else if ((res = currPrompt.handleChoice(this, input.in()))
                                .type() == ChoicePrompt.ResultType.INVALID) {
                            // Invalid choice
                            var invalidResult = (Prompt.InvalidResult) res;
                            errorMsg = invalidResult.errorMsg() == null ? "Invalid input " + input.in()
                                    : invalidResult.errorMsg();
                        } else if (res.type() == ChoicePrompt.ResultType.PROMPT) {
                            // Replace the current prompt with the new one
                            var promptResult = (Prompt.PromptResult) res;
                            promptStack.clear();
                            promptStack.push(currPrompt = promptResult.prompt());
                            errorMsg = promptResult.errorMsg() == null ? "" : promptResult.errorMsg();
                        } else if (res.type() == ChoicePrompt.ResultType.SUBPROMPT) {
                            // Add a new subprompt
                            var promptResult = (Prompt.PromptResult) res;
                            promptStack.push(currPrompt = promptResult.prompt());
                            errorMsg = promptResult.errorMsg() == null ? "" : promptResult.errorMsg();
                        } else /* if(res.type() == Prompt.ResultType.DONE) */ {
                            // Delete all until we get to the parent
                            while (promptStack.size() > 1)
                                promptStack.pop();
                            currPrompt = promptStack.peek();
                            errorMsg = "";
                        }
                    }
                }

                out.cursor(0, 0);
                out.eraseInDisplay();

                if (scene != null)
                    scene.render(out);

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
            LOGGER.error("Uncaught exception in TuiRenderer render thread", t);
        }
    }

    private void inputLoop(Reader reader) {
        final var sc = new Scanner(reader);
        try {
            while (!Thread.currentThread().isInterrupted() && sc.hasNextLine())
                events.add(new InputEvent(sc.nextLine().strip()));
        } catch (Throwable t) {
            LOGGER.error("Uncaught exception in TuiRenderer render thread", t);
        }
    }

    @SuppressWarnings("BusyWait") // Done on purpose
    private void resizeLoop(TuiPrintStream out) {
        try {
            var timeoutMillis = 1000 / RESIZE_PER_SECOND;
            var oldSize = out.getTerminalSize();
            while (!Thread.currentThread().isInterrupted()) {
                var size = out.getTerminalSize();
                if (!oldSize.equals(size))
                    rerender();
                oldSize = size;
                Thread.sleep(timeoutMillis);
            }
        } catch (InterruptedException t) {
            // Thread got stopped, normal control flow
        } catch (Throwable t) {
            LOGGER.error("Uncaught exception in TuiRenderer resize thread", t);
        }
    }

    @Override
    public void close() {
        renderThread.interrupt();
        inputThread.interrupt();
        resizeThread.interrupt();
    }

    /** Request that the TUI is re-rendered sometime in the future */
    public void rerender() {
        events.add(RenderEvent.INSTANCE);
    }

    /**
     * Set the current scene to be rendered, replacing any already existing one
     * <p>
     * There can be only 1 scene active at any time
     *
     * @param scene scene to render
     */
    public void setScene(TuiScene scene) {
        events.add(new SetSceneEvent(scene));
    }

    /**
     * Set the current root prompt to be rendered and interacted by the user,
     * replacing any already existing one.
     * <p>
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

    private record SetSceneEvent(@Nullable TuiScene scene) implements Event {
    }

    private record SetPromptEvent(Prompt prompt) implements Event {
    }

    private record RenderEvent() implements Event {
        static RenderEvent INSTANCE = new RenderEvent();
    }
}
