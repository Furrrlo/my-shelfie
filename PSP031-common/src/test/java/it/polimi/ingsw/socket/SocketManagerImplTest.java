package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.AckPacket;
import it.polimi.ingsw.socket.packets.Packet;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SocketManagerImplTest {

    @Test
    void testInboundPacketsNotDiscarded() throws IOException, InterruptedException {
        var executor = Executors.newFixedThreadPool(2);
        try (var pipe = new PipedInputStream();
             var mgrOs = new ObjectOutputStream(new PipedOutputStream(pipe));
             var pipe2 = new PipedInputStream();
             var testOos = new ObjectOutputStream(new PipedOutputStream(pipe2));
             var mgrIs = new ObjectInputStream(pipe2)) {

            double seed = Math.random();
            try (final var mgr = new SocketManagerImpl<>("test", executor, mgrIs, mgrOs, 1, TimeUnit.SECONDS)) {
                testOos.writeObject(new SimpleSeqPacket(new TestPacketReq(seed), 10));
                Thread.sleep(2000);

                var ctx = mgr.receive(TestPacketReq.class);
                assertEquals(seed, ctx.getPacket().rnd());
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    @SuppressWarnings("RedundantExplicitClose") // It's the whole point of the test
    void testSubsequentCloses() throws IOException {
        var executor = Executors.newFixedThreadPool(2);
        try (var pipe = new PipedInputStream();
             var mgrOs = new ObjectOutputStream(new PipedOutputStream(pipe));
             var pipe2 = new PipedInputStream();
             var ignored = new ObjectOutputStream(new PipedOutputStream(pipe2));
             var mgrIs = new ObjectInputStream(pipe2)) {

            try (final var mgr = new SocketManagerImpl<>("test", executor, mgrIs, mgrOs, 1, TimeUnit.SECONDS)) {
                mgr.close();
                mgr.close();
                mgr.close();
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testMethodsAfterClose() throws IOException {
        var executor = Executors.newFixedThreadPool(2);
        try (var pipe = new PipedInputStream();
             var mgrOs = new ObjectOutputStream(new PipedOutputStream(pipe));
             var pipe2 = new PipedInputStream();
             var ignored = new ObjectOutputStream(new PipedOutputStream(pipe2));
             var mgrIs = new ObjectInputStream(pipe2)) {

            final var mgr = new SocketManagerImpl<>("test", executor, mgrIs, mgrOs, 1, TimeUnit.SECONDS);
            mgr.close();
            assertAll(
                    () -> {
                        var ex = assertThrows(IOException.class, () -> mgr.send(new TestPacketReq(Math.random())));
                        assertEquals(SocketManagerImpl.CLOSE_EX_MSG, ex.getMessage());
                    },
                    () -> {
                        var ex = assertThrows(IOException.class,
                                () -> mgr.send(new TestPacketReq(Math.random()), AckPacket.class));
                        assertEquals(SocketManagerImpl.CLOSE_EX_MSG, ex.getMessage());
                    },
                    () -> {
                        var ex = assertThrows(IOException.class, () -> mgr.receive(AckPacket.class));
                        assertEquals(SocketManagerImpl.CLOSE_EX_MSG, ex.getMessage());
                    });
        } finally {
            executor.shutdown();
        }
    }

    private record TestPacketReq(@SuppressWarnings("unused") double rnd) implements Packet {
    }
}