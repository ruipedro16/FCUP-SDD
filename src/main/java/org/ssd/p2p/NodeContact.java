package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.InetAddress;

@Data
public class NodeContact{
    private long seen;
    private byte[] id;
    private final InetAddress address;
    private int port;

    public NodeContact(byte[] id, @NonNull InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public NodeContact(@NonNull InetAddress address) {
        this.address = address;
    }
}
