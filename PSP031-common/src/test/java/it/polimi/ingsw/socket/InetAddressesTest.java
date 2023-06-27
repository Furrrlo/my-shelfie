package it.polimi.ingsw.socket;

import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class InetAddressesTest {
    @Test
    void assertNonDnsReversableWorking() throws UnknownHostException {
        var addrStr = "127.0.0.1";
        var addr = InetAddresses.createNonDnsReversable(addrStr);
        assertEquals(addrStr, assertTimeoutPreemptively(Duration.ofMillis(10), addr::getHostName));
    }
}