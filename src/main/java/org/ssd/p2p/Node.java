package org.ssd.p2p;

import lombok.Getter;
import org.ssd.constants.KademliaConstants;

import java.util.Random;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class Node {

    private static final Random random = new Random();
    @Getter
    private final byte[] id;

    @Getter
    private final int port;

    @Getter
    private long seen;

    //add k_buckets
    private final RoutingTable routingTable;

    //add a channel manager for this node or create it here alongside the keys

    /**
     * Node constructor
     * @param id node id
     * @param port node port
     */
    public Node(byte[] id, int port /*add key or channel manager here*/) {
        this.id = id;
        this.port = port;
        //init k_buckets
        this.routingTable = new RoutingTable(id); // initializes the routingtable & k buckets
        //init the rest
    }

    /**
     * Constructor with omitted nodeId. It generates a random one.
     * @param port node port
     */
    public Node(int port) {
        byte[] genId = new byte[KademliaConstants.B];
        random.nextBytes(genId);
        this.id = genId;
        this.port = port;
        this.routingTable = new RoutingTable(genId); // initializes the routingtable & k buckets
    }

    public static byte[] getDistance(byte[] id1, byte[] id2) {
        if (id1.length != KademliaConstants.B || id2.length != KademliaConstants.B) {
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
