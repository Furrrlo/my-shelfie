package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InterruptibleInputStream extends BufferedInputStream {

    private final InterruptibleStream iin;
    private final InputStream underlyingStream;
    private final TransferQueue<LockAndFuture<Integer>> queue;

    @SuppressWarnings("resource") // The thread is demon and cannot be interrupted anyway
    private InterruptibleInputStream(InputStream in, ThreadFactory threadFactory) {
        super(new InterruptibleStream(in));

        this.iin = (InterruptibleStream) this.in;
        this.underlyingStream = iin.underlyingStream;
        this.queue = iin.queue;

        Executors.newSingleThreadExecutor(runnable -> {
            var th = threadFactory.newThread(runnable);
            th.setDaemon(true);
            return th;
        }).submit(this::readLoop);
    }

    public static InterruptibleInputStream wrap(InputStream is, ThreadFactory threadFactory) {
        if (is instanceof InterruptibleInputStream iis)
            return iis;

        return new InterruptibleInputStream(is, threadFactory);
    }

    public static InterruptibleInputStream wrap(InputStream is) {
        if (is instanceof InterruptibleInputStream iis)
            return iis;

        return new InterruptibleInputStream(is, Thread.ofPlatform().factory());
    }

    private void readLoop() {
        try {
            readLoop: while (!Thread.currentThread().isInterrupted()) {
                var lockAndFuture = new LockAndFuture<>(new ReentrantLock(), new CompletableFuture<Integer>());
                queue.transfer(lockAndFuture);
                try {
                    var ch = underlyingStream.read();
                    do {
                        // Check that the future is not cancelled, lock to make sure it
                        // does not get cancelled between our check and the completion
                        boolean cancelled;
                        lockAndFuture.lock().lock();
                        try {
                            cancelled = lockAndFuture.future().isCancelled();
                            if (!cancelled) {
                                lockAndFuture.future().complete(ch);
                                continue readLoop;
                            }
                        } finally {
                            lockAndFuture.lock().unlock();
                        }

                        lockAndFuture = new LockAndFuture<>(new ReentrantLock(), new CompletableFuture<>());
                        queue.transfer(lockAndFuture);
                    } while (true);
                } catch (IOException e) {
                    lockAndFuture.future().completeExceptionally(e);
                }
            }
        } catch (InterruptedException ignored) {
            // We got interrupted, we are done
        }
    }

    public void configureDefaultTimeout(@Range(from = 1, to = Integer.MAX_VALUE) long timeout, TimeUnit timeoutUnit) {
        iin.configureDefaultTimeout(timeout, timeoutUnit);
    }

    public void clearDefaultTimeout() {
        iin.clearDefaultTimeout();
    }

    private static class InterruptibleStream extends InputStream {

        private final TransferQueue<LockAndFuture<Integer>> queue = new LinkedTransferQueue<>();
        private final InputStream underlyingStream;

        private long timeout = 0;
        private @Nullable TimeUnit timeoutUnit;

        public InterruptibleStream(InputStream underlyingStream) {
            this.underlyingStream = underlyingStream;
        }

        public synchronized void configureDefaultTimeout(@Range(from = 1, to = Integer.MAX_VALUE) long timeout,
                                                         TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
        }

        public synchronized void clearDefaultTimeout() {
            this.timeout = 0;
            this.timeoutUnit = null;
        }

        public boolean hasSetTimeout() {
            return timeout > 0 && timeoutUnit != null;
        }

        @Override
        public synchronized int read() throws IOException {
            try {
                if (hasSetTimeout()) {
                    Objects.requireNonNull(timeoutUnit, "timeoutUnit should be checked by hasSetTimeout()");

                    var lockAndFuture = queue.poll(timeout, timeoutUnit);
                    if (lockAndFuture == null)
                        throw new InterruptedByTimeoutException();

                    try {
                        return lockAndFuture.future().get(timeout, timeoutUnit);
                    } catch (TimeoutException e) {
                        // Try to acquire the lock to cancel it
                        lockAndFuture.lock().lock();
                        try {
                            // If once acquired the lock, the future is still not done
                            // cancel it and throw an interruption exception
                            Integer res;
                            if ((res = lockAndFuture.future().getNow(null)) == null) {
                                lockAndFuture.future().cancel(true);
                                throw (IOException) new InterruptedByTimeoutException().initCause(e);
                            }
                            // Found a value right before cancelling, we are lucky
                            return res;
                        } finally {
                            lockAndFuture.lock().unlock();
                        }
                    }
                }

                return queue.take().future().get();
            } catch (InterruptedException e) {
                throw (IOException) new InterruptedIOException().initCause(e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof IOException)
                    throw new IOException(e.getCause());
                throw new UnsupportedOperationException("Unexpected exception", e);
            }
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            Objects.checkFromIndexSize(off, len, b.length);
            if (len == 0)
                return 0;

            int c = read();
            if (c == -1)
                return -1;

            b[off] = (byte) c;

            var oldTimeout = this.timeout;
            var oldTimeoutUnit = this.timeoutUnit;

            int i = 1;
            try {
                if (!hasSetTimeout())
                    configureDefaultTimeout(100, TimeUnit.MILLISECONDS);

                for (; i < len; i++) {
                    c = read();
                    if (c == -1) {
                        break;
                    }
                    b[off + i] = (byte) c;
                }

                this.timeout = oldTimeout;
                this.timeoutUnit = oldTimeoutUnit;
            } catch (IOException ee) {
                // Ignored
            }
            return i;
        }

        @Override
        public void close() throws IOException {
            underlyingStream.close();
        }
    }

    private record LockAndFuture<T>(Lock lock, CompletableFuture<T> future) {
    }
}
