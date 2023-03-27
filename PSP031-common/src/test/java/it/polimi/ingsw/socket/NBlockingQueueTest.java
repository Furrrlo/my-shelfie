package it.polimi.ingsw.socket;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class NBlockingQueueTest {

    @Test
    void testAddSingleThreaded() throws ExecutionException, InterruptedException, TimeoutException {

        final NBlockingQueue<Object> e = new NBlockingQueue<>();

        final CountDownLatch addFromTh1 = new CountDownLatch(1);
        final CompletableFuture<Void> addFromTh1Res = new CompletableFuture<>();

        final CountDownLatch addFromTh2 = new CountDownLatch(1);
        final CompletableFuture<Void> addFromTh2Res = new CompletableFuture<>();

        final CountDownLatch readdFromTh1 = new CountDownLatch(1);
        final CompletableFuture<Void> readdFromTh1Res = new CompletableFuture<>();

        var th1 = new Thread(() -> {
            try {
                addFromTh1.await();
                e.add(new Object());
                addFromTh1Res.complete(null);
            } catch (Throwable t) {
                addFromTh1Res.completeExceptionally(t);
            }

            try {
                readdFromTh1.await();
                e.add(new Object());
                readdFromTh1Res.complete(null);
            } catch (Throwable t) {
                readdFromTh1Res.completeExceptionally(t);
            }
        });

        var th2 = new Thread(() -> {
            try {
                addFromTh2.await();
                e.add(new Object());
                addFromTh2Res.complete(null);
            } catch (Throwable t) {
                addFromTh2Res.completeExceptionally(t);
            }
        });

        try {
            th1.start();
            th2.start();

            addFromTh1.countDown();
            addFromTh1Res.get(1000, TimeUnit.MILLISECONDS);

            addFromTh2.countDown();
            ExecutionException ex = assertThrows(ExecutionException.class,
                    () -> addFromTh2Res.get(1000, TimeUnit.MILLISECONDS));
            assertThrows(UnsupportedOperationException.class, () -> {
                throw ex.getCause();
            });

            readdFromTh1.countDown();
            readdFromTh1Res.get(1000, TimeUnit.MILLISECONDS);
        } finally {
            th1.interrupt();
            th2.interrupt();
        }
    }

    @Test
    void testLargeConsumerHole() throws ExecutionException, InterruptedException, TimeoutException {
        final var threadNum = 1000;
        final var executorService = Executors.newFixedThreadPool(threadNum);

        final NBlockingQueue<Object> queue = new NBlockingQueue<>();
        try {
            CompletableFuture<Object> last = null;
            var allExceptLast = new ArrayList<CompletableFuture<Object>>();

            final var toSupply = new ArrayList<>(); // Exact objects that the consumers want, in order
            for (int i = 0; i < threadNum; i++) {
                Object obj = new Object();
                toSupply.add(obj);

                var hasRegistered = new CountDownLatch(1);
                var promise = CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return queue.takeFirstMatching(o -> o == obj, hasRegistered::countDown);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        executorService);

                if (i == threadNum - 1)
                    last = promise;
                else
                    allExceptLast.add(promise);

                if (!hasRegistered.await(1000, TimeUnit.MILLISECONDS))
                    fail("Couldn't start thread n " + i);
            }

            // Supply the first threadNum - 1 consumers, so that they all set themselves
            // as done, but bc the ones before them are not done yet, they won't be able to
            // be deleted
            for (int i = threadNum - 2; i >= 0; i--)
                queue.add(toSupply.get(i));
            CompletableFuture.allOf(allExceptLast.toArray(CompletableFuture[]::new)).get(1000, TimeUnit.MILLISECONDS);
            Thread.sleep(2000);

            // Supply the last and see if the mechanism works
            System.out.println(queue.getQueueDebugState());
            queue.add(toSupply.get(threadNum - 1));
            assertEquals(
                    toSupply.get(threadNum - 1),
                    Objects.requireNonNull(last).get(1000, TimeUnit.MILLISECONDS));
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    void stressTestConcurrency() throws Throwable {
        final var threadNum = 1000;
        final var executorService = Executors.newFixedThreadPool(threadNum);

        final NBlockingQueue<Object> queue = new NBlockingQueue<>();
        try {
            var promises = new ArrayList<CompletableFuture<Void>>();

            final var toSupply = new ArrayList<>(); // Exact objects that the consumers want, in order
            for (int i = 0; i < threadNum; i++) {
                Object obj = new Object();
                toSupply.add(obj);
                promises.add(CompletableFuture.runAsync(
                        () -> {
                            try {
                                assertSame(obj, queue.takeFirstMatching(o -> o == obj));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        executorService));
            }

            final var rnd = new Random();
            while (!toSupply.isEmpty())
                queue.add(toSupply.remove(rnd.nextInt(toSupply.size())));

            CompletableFuture
                    .allOf(promises.toArray(CompletableFuture[]::new))
                    .get(10000, TimeUnit.MILLISECONDS);
        } finally {
            executorService.shutdown();
        }
    }
}