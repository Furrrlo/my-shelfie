package it.polimi.ingsw.socket;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Queue used for implementing 1 producer to n consumers, where the consumers can
 * retrieve elements off of the queue by matching them against a {@link Predicate}.
 *
 * @param <E> the type of elements held in this queue
 */
public class NBlockingQueue<E> {

    /*
     * This is implemented as a lock-free linked queue, where each node represent
     * a consumer and has its own BlockingQueue of objects which it has already processed.
     *
     * Objects to consume are therefore added to the processed queue of the head,
     * the subsequent node will then be able to process the objects itself, then add it to its
     * own processed queue, and so on and so forth.
     *
     * Nodes are not allowed to delete themselves, but are only allowed to signal that they are done.
     * Once that happens, the marked node transfers the entire ownership of its fields to the subsequent one,
     * which will be in charge of clearing the processed objects queue and unlinking it from its prev.
     */

    private class Node {

        private volatile @Nullable Node prev;
        private final AtomicReference<Node> next = new AtomicReference<>();
        private final BlockingQueue<E> processed = new LinkedBlockingQueue<>();
        private volatile boolean done;

        public void done() {
            done = true;
            processed.add(signalDone);
        }

        public void addToNext(E e) {
            processed.add(e);
        }
    }

    @SuppressWarnings("unchecked") // Won't be returned outside, so we can safely cast it
    private final E signalDone = (E) new Object() {
        @Override
        public String toString() {
            return "signalDone";
        }
    };

    private @Nullable Thread producerThread;
    private final Node head = new Node();
    private volatile Node tail = head;

    /**
     * Inserts the specified element at the tail of this queue.
     *
     * @throws NullPointerException if the specified element is null
     */
    public void add(E e) {
        if (producerThread == null)
            producerThread = Thread.currentThread();
        else if (Thread.currentThread() != producerThread)
            throw new UnsupportedOperationException("There can only be 1 producer");

        head.addToNext(Objects.requireNonNull(e));
    }

    /**
     * Retrieves and removes the first element of the queue which matches the given predicate, waiting if
     * necessary until an element becomes available.
     *
     * @return the first element which matches the given predicate
     * @throws InterruptedException if interrupted while waiting
     */
    public E takeFirstMatching(Matcher<E> matcher) throws InterruptedException {
        try {
            return takeFirstMatching(matcher, null, -1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new AssertionError("There should be no timeout when -1 is passed", e);
        }
    }

    public E takeFirstMatching(Matcher<E> matcher, long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {
        return takeFirstMatching(matcher, null, timeout, unit);
    }

    /**
     * @param timeout time before a {@link TimeoutException} is thrown, using the given {@code unit}.
     *        If -1, no timeout is used and TimeoutException is never thrown
     * @param unit time unit for {@code timeout}
     * @throws TimeoutException if timeout is not -1 and has elapsed
     */
    @VisibleForTesting
    E takeFirstMatching(Matcher<E> matcher,
                        @Nullable Runnable signalRegistered,
                        long timeout,
                        TimeUnit unit)
            throws InterruptedException, TimeoutException {
        var newNode = new Node();
        // Fight to get added as tail
        // Note that, because of the impl, there is no way for the tail to remove itself
        // as a node can only be deleted by a subsequent node, which the tail by definition can't have
        // Therefore, we can skip any check to see if we fell of the linked list somehow
        Node prev;
        for (prev = tail;; prev = tail) {
            if (prev.next.compareAndSet(null, newNode))
                break;
            // Lost the CAS fight, just retry
        }
        // We won, newNode is officially part of the queue now
        newNode.prev = prev;
        tail = newNode;
        if (signalRegistered != null)
            signalRegistered.run();

        // Consuming cycle
        var timeoutMillis = timeout != -1 ? unit.toMillis(timeout) : -1;
        var startTimeMillis = System.currentTimeMillis();
        outer: for (;;) {
            // The head can't have done = true, so prev != head is implicit
            while (prev.done /* && prev != head */) {
                // The previous node is done, process all its enqueued stuff
                // Note: by signaling that it's finished, the previous node won't modify its processed queue anymore.
                //       We are free to empty it and no additional nodes will be added once we are done
                E toTransfer;
                while ((toTransfer = prev.processed.poll()) != null) {
                    // We already knew that the prev queue was done, we don't care
                    if (toTransfer == signalDone)
                        continue;

                    var processResult = matcher.apply(toTransfer, ProcessResultCtx.INSTANCE);
                    if (processResult == ProcessResult.CONSUME || processResult == ProcessResult.PEEK) {
                        // We want to get it but not remove it, so also pass it along
                        if (processResult == ProcessResult.PEEK)
                            newNode.addToNext(toTransfer);

                        newNode.done();
                        return toTransfer;
                    }
                    // We don't care about this element, pass it along
                    newNode.addToNext(toTransfer);
                }
                // Processed all its stuff, it's done and empty, un-reference it
                Node prevPrev;
                if ((prevPrev = prev.prev) != null)
                    prevPrev.next.set(newNode);
                newNode.prev = prev = Objects.requireNonNull(prev.prev, "Only head can have prev = null");
            }

            while (true) {
                var elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                if (timeoutMillis != -1 && elapsedMillis >= timeoutMillis)
                    throw new TimeoutException("Timeout expired");

                final E candidate;
                if (timeoutMillis == -1) {
                    candidate = prev.processed.take();
                } else {
                    candidate = prev.processed.poll(timeoutMillis - elapsedMillis, TimeUnit.MILLISECONDS);
                    if (candidate == null)
                        throw new TimeoutException("Timeout expired");
                }

                // Prev queue just switched to done, let's return to the previous loop
                if (candidate == signalDone)
                    continue outer;

                var processResult = matcher.apply(candidate, ProcessResultCtx.INSTANCE);
                if (processResult == ProcessResult.CONSUME || processResult == ProcessResult.PEEK) {
                    // We want to get it but not remove it, so also pass it along
                    if (processResult == ProcessResult.PEEK)
                        newNode.addToNext(candidate);

                    newNode.done();
                    return candidate;
                }
                // We don't care about this element, pass it along
                newNode.addToNext(candidate);
            }
        }
    }

    @VisibleForTesting
    String getQueueDebugState() {
        StringBuilder sb = new StringBuilder();
        var curr = head;
        while (curr != null) {
            var next = curr.next.get();
            sb.append(next == null ? String.valueOf(curr.done) : curr.done + ", ");
            curr = next;
        }
        return sb.toString();
    }

    public interface Matcher<E> extends BiFunction<E, ProcessResultCtx, ProcessResult> {
    }

    enum ProcessResult {
        PEEK,
        CONSUME,
        SKIP
    }

    public static class ProcessResultCtx {

        private static final ProcessResultCtx INSTANCE = new ProcessResultCtx();

        private ProcessResultCtx() {
        }

        public ProcessResult peek() {
            return ProcessResult.PEEK;
        }

        public ProcessResult consume() {
            return ProcessResult.CONSUME;
        }

        public ProcessResult skip() {
            return ProcessResult.SKIP;
        }
    }
}
