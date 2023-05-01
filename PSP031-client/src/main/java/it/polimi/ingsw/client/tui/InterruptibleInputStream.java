package it.polimi.ingsw.client.tui;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.*;

public class InterruptibleInputStream extends BufferedInputStream {

    private final InterruptibleStream iin;
    private final InputStream underlyingStream;
    private final TransferQueue<Object> queue;

    @SuppressWarnings("resource") // The thread is demon and cannot be interrupted anyway
    private InterruptibleInputStream(InputStream in, ThreadFactory threadFactory) {
        super(new InterruptibleStream());

        this.underlyingStream = in;
        this.iin = (InterruptibleStream) this.in;
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
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    queue.transfer(underlyingStream.read());
                } catch (IOException e) {
                    queue.transfer(e);
                }
            }
        } catch (InterruptedException ignored) {
            // We got interrupted, we are done
        }
    }

    public void configureDefaultTimeout(@Range(from = 1, to = Integer.MAX_VALUE) long timeout, TimeUnit timeoutUnit) {
        iin.timeout = timeout;
        iin.timeoutUnit = timeoutUnit;
    }

    public void clearDefaultTimeout() {
        iin.timeout = 0;
        iin.timeoutUnit = null;
    }

    static class InterruptibleStream extends InputStream {

        private final TransferQueue<Object> queue = new LinkedTransferQueue<>();

        private long timeout = 0;
        private @Nullable TimeUnit timeoutUnit;

        public InterruptibleStream() {
        }

        @Override
        public int read() throws IOException {
            Object res;
            try {
                if (timeout > 0 && timeoutUnit != null)
                    res = queue.poll(timeout, timeoutUnit);
                else
                    res = queue.take();
            } catch (InterruptedException e) {
                throw (IOException) new InterruptedIOException().initCause(e);
            }

            if (res == null)
                throw new InterruptedByTimeoutException();
            if (res instanceof Integer)
                return (int) res;
            if (res instanceof IOException)
                throw new IOException((IOException) res);
            throw new UnsupportedOperationException("Unexpected res type " + res);
        }
    }
}
