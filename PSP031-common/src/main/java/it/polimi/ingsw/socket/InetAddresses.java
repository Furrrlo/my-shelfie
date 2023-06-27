package it.polimi.ingsw.socket;

import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** Utilities for working with {@link InetAddress}es */
public class InetAddresses {

    private InetAddresses() {
    }

    /**
     * Force the creation of a InetAddress which will not in any case trigger a DNS reverse lookup
     *
     * @param host the specified host, or null.
     * @return an IP address for the given host name.
     * @throws UnknownHostException if no IP address for the
     *         {@code host} could be found, or if a scope_id was specified
     *         for a global IPv6 address.
     * @see InetAddress#getByName(String)
     */
    public static InetAddress createNonDnsReversable(@Nullable String host) throws UnknownHostException {
        return InetAddress.getByAddress(host, InetAddress.getByName(host).getAddress());
    }
}
