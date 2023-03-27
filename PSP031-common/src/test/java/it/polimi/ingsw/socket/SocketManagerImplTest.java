package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.packets.Packet;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SocketManagerImplTest {

    @Test
    void testInboundPacketsNotDiscarded() throws IOException, InterruptedException {
        try (var pipe = new PipedInputStream();
             var mgrOs = new ObjectOutputStream(new PipedOutputStream(pipe));
             var pipe2 = new PipedInputStream();
             var testOos = new ObjectOutputStream(new PipedOutputStream(pipe2));
             var mgrIs = new ObjectInputStream(pipe2)) {

            double seed = Math.random();
            final var mgr = new SocketManagerImpl<>("test", mgrIs, mgrOs);
            testOos.writeObject(new SimpleSeqPacket(new TestPacketReq(seed), 10));
            Thread.sleep(2000);

            var ctx = mgr.receive(TestPacketReq.class);
            assertEquals(seed, ctx.getPacket().rnd());

            // TODO: close SocketManagerImpl properly
        }
    }

    private record TestPacketReq(double rnd) implements Packet {
    }
}