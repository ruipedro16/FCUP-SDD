package org.ssd.p2p;

import lombok.Getter;

public class Node {

    @Getter
    private final byte[] id;

    @Getter
    private final int port;

    @Getter
    private long seen;
    //add k_buckets
    //add a channel manager for this node or create it here alongside the keys
    public Node(byte[] id, int port /*add key or channel manager here*/) {
        this.id = id;
        this.port = port;
        //init k_buckets
        //init the rest
    }

    /**
     * Function receives the necessary info to place this node into a kademlia network
     */
    public void init() {
        //todo
    }

}
