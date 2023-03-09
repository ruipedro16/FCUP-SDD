package org.ssd.p2p;

import lombok.Getter;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class Node {
    private static final int NODE_ID_LEN = 160; // length of the node id, in bits              160 bits: Output of SHA-1

    @Getter
    private final byte[] id;

    @Getter
    private final int port;

    @Getter
    private long seen;

    //add k_buckets
    private final RoutingTable routingTable;

    //add a channel manager for this node or create it here alongside the keys
    public Node(byte[] id, int port /*add key or channel manager here*/) {
        this.id = id;
        this.port = port;
        //init k_buckets
        this.routingTable = new RoutingTable(id); // initializes the routingtable & k buckets
        //init the rest
    }

    public static byte[] getDistance(byte[] id1, byte[] id2) {
        if (id1.length != NODE_ID_LEN || id2.length != NODE_ID_LEN) {
            throw new IllegalArgumentException();
        }

        byte[] res = new byte[id1.length];

        for (int i = 0; i < id1.length; i++) {
            res[i] = (byte) (id1[i] ^ id2[i]);
        }

        return res;
    }


    /*
     * Counts the number of 0s in the beginning of a byte sequence
     */
    public static int getPrefixLengh(byte[] seq) {
        return 0; // todo: MOve to utils
    }

    /*
     * Given two node IDs,
     */
    public static int getBucket(byte[] currentNodeId, byte[] other) {
        byte[] distance = getDistance(currentNodeId, other);
        return getPrefixLengh(distance);
    }

    @Override
    public String toString() {
        return Hex.toHexString(id);
    }

    /**
     * Function receives the necessary info to place this node into a kademlia network
     */
    public void init() {
        //todo
    }
}
