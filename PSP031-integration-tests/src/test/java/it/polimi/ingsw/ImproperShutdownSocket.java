package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImproperShutdownSocket extends Socket {

    private final Lock readLock = new ReentrantLock();
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
        readLock.lock();
        discardOutput = true;
    }

    public synchronized void actuallyClose() throws IOException {
        super.close();
    }

    private class BlockableInputStream extends FilterInputStream {

        protected BlockableInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            try {
                readLock.lockInterruptibly();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            try {
                readLock.lockInterruptibly();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read(b);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            try {
                readLock.lockInterruptibly();
            } catch (InterruptedException e) {
                throw (IOException) new ClosedByInterruptException().initCause(e);
            }

            try {
                return super.read(b, off, len);
            } finally {
                readLock.unlock();
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
