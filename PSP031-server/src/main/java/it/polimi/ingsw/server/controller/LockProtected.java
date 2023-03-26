package it.polimi.ingsw.server.controller;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockProtected<T> {

    private static final LockCloseable<?> NULL_LOCK_CLOSEABLE = new LockCloseable<>() {
        @Override
        @SuppressWarnings("NullAway") // NullAway doesn't support generics
        public @Nullable Object obj() {
            return null;
        }

        @Override
        public void close() {
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> LockCloseable<@Nullable T> useNullable(@Nullable LockProtected<T> lock) {
        return lock == null ? (LockCloseable<T>) NULL_LOCK_CLOSEABLE : lock.use();
    }

    private final T obj;
    private final Lock lock;

    private final LockCloseable<T> lockCloseable = new LockCloseable<>() {
        @Override
        public T obj() {
            return obj;
        }

        @Override
        public void close() {
            lock.unlock();
        }
    };

    public LockProtected(T obj) {
        this(obj, new ReentrantLock());
    }

    public LockProtected(T obj, Lock lock) {
        this.obj = obj;
        this.lock = lock;
    }

    public Lock getLock() {
        return lock;
    }

    public LockCloseable<T> use() {
        lock.lock();
        return lockCloseable;
    }

    @VisibleForTesting
    public T getUnsafe() {
        return obj;
    }

    @Override
    public String toString() {
        return "LockProtected{" +
                "obj=" + obj +
                ", lock=" + lock +
                '}';
    }

    public interface LockCloseable<T> extends Closeable {

        T obj();

        @Override
        void close();
    }
}
