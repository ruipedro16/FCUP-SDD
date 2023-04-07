package org.ssd.p2p;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.InetAddress;

@Data
@RequiredArgsConstructor
public class NodeContact{
    private long seen;
    private final byte[] id;
    private final InetAddress address;
    private final int port;
}
