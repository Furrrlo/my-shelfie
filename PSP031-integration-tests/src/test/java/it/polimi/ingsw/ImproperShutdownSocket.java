package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.Semaphore;

public class ImproperShutdownSocket extends Socket {

    private final Semaphore readSemaphore = new Semaphore(Integer.MAX_VALUE);
    private volatile boolean discardOutput;

    @Override
    public InputStream getInputStream() throws IOException {
        return new BlockableInputStream(super.getInputStream());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new NullableOutputStream(super.getOutputStream());
    }

    @Override
    public synchronized void close() {
        System.out.println("closing...");
        if (discardOutput) //Already closed
            return;
        readSemaphore.drainPermits();
        System.out.println("locked");
        discardOutput = true;
    }

    public synchronized void actuallyClose() throws IOException {
        super.close();
        System.out.println("Actually closed");
    }

    private class BlockableInputStream extends FilterInputStream {

        protected BlockableInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            try {
                readSemaphore.acquire();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read();
            } finally {
                readSemaphore.release();
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            try {
                readSemaphore.acquire();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read(b);
            } finally {
                readSemaphore.release();
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            try {
                readSemaphore.acquire();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read(b, off, len);
            } finally {
                readSemaphore.release();
            }
        }

        @Override
        public void close() {
        }
    }

    private class NullableOutputStream extends FilterOutputStream {

        public NullableOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            if (!discardOutput)
                super.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (!discardOutput)
                super.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (!discardOutput)
                super.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            if (!discardOutput)
                super.flush();
        }

        @Override
        public void close() {
        }
    }
}
