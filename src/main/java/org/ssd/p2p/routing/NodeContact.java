package org.ssd.p2p.routing;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bouncycastle.util.encoders.Hex;

import java.net.InetAddress;
import java.util.Arrays;

@Getter
@Setter
public class NodeContact {
    private final InetAddress address;
    private final int port;
    private final byte[] id;

    private long lastSeen;
    private int staleCount;

    public NodeContact(@NonNull InetAddress address, int port, byte[] id, long lastSeen) {
        this.address = address;
        this.port = port;
        this.id = id;
        this.lastSeen = lastSeen;
        this.staleCount = 0;
    }

    /**
     * A stale node refers to a node that is no longer considered to be a part of the network.
     * This can happen when a node fails to respond to ping messages sent by other nodes in the network, or when it is
     * found to be offline. When a node is marked as stale, it is removed from the routing table of other nodes in the network
     */
    public void incrementStaleNodesCount() {
        this.staleCount++;
    }

    public void resetStaleNodesCount() {
        this.staleCount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeContact that = (NodeContact) o;
        return this.port == that.port && this.address.equals(that.address) && Arrays.equals(id, that.id);
    }

    @Override
    public String toString() {
        return "(" + Hex.toHexString(this.id) + ", " + this.address + ", " + this.port + ")\n";
    }
}
