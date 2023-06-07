package it.polimi.ingsw.server.controller;

import com.google.errorprone.annotations.MustBeClosed;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represent an object which should only be accessed through the use of a lock
 * 
 * @param <T> type of the held object
 */
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

    /**
     * Utility method to allow using a possibly null LockProtected object in a try-with-resources
     * <p>
     * If the object is not null, this will call {@link LockProtected#use()} and, therefore, take
     * the lock. Otherwise, it will do nothing.
     *
     * @param lock the possibly null LockProtected object
     * @return a non-null LockCloseable<@Nullable T>
     * @param <T> type of the held object
     */
    @MustBeClosed
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

    /**
     * Acquires the lock.
     * <p>
     * If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     *
     * @return closeable which will unlock the lock automatically once closed
     * @see Lock#lock()
     */
    @MustBeClosed
    public LockCloseable<T> use() {
        lock.lock();
        return lockCloseable;
    }

    /** Returns the protected object without acquiring the lock */
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

    /**
     * Closeable which will unlock the lock automatically once closed
     * 
     * @param <T> type of the held object
     */
    public interface LockCloseable<T> extends Closeable {

        /** Returns the object we are currently holding a lock for */
        T obj();

        @Override
        void close();
    }
}
