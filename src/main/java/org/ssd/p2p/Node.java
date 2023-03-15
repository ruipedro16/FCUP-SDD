package org.ssd.p2p;

import lombok.Data;
import lombok.Getter;
import org.ssd.constants.KademliaConstants;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

@Data
public class Node {

    private static final Random random = new Random();
    private final byte[] id;

    private final int port;
    private final InetAddress address;

    private long seen;

    //add k_buckets
    // private final RoutingTable routingTable;

    //add a channel manager for this node or create it here alongside the keys

    /**
     * Node constructor
     *
     * @param id   node id
     * @param port node port
     */
    public Node(byte[] id, int port /*add key or channel manager here*/) {
        this.id = id;
        this.port = port;
        //init k_buckets
        // this.routingTable = new RoutingTable(id); // initializes the routingtable & k buckets
        //init the rest
        this.address = null; // todo: change this
    }

    /**
     * Constructor with omitted nodeId. It generates a random one.
     *
     * @param port node port
     */
    public Node(int port) {
        byte[] genId = new byte[KademliaConstants.B];
        random.nextBytes(genId);
        this.id = genId;
        this.port = port;
        // this.routingTable = new RoutingTable(genId); // initializes the routingtable & k buckets
        this.address = null; // todo: change this
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
     *
     * Probably wrong
     */
    public static int getPrefixLength(byte[] seq) {
        int pref = 0;
        if (seq.length == 0) {
            return pref;
        }

        byte b = seq[0];
        if (b == 0) {
            pref = 8;
        } else {
            int extras = 0;
            for (int i = 7; i >= 0; i--) {
                boolean a = (b & (1 << i)) == 0;
                if (a) {
                    extras++;
                } else {
                    break;
                }
            }
            pref = extras;
        }
        return pref;
    }

    /*
     * Given two node IDs,
     */
    public static int getBucket(byte[] currentNodeId, byte[] other) {
        byte[] distance = getDistance(currentNodeId, other);
        return getPrefixLength(distance);
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

    public boolean ping(byte[] nodeId, InetAddress address, int port) {
        return true;
    }

    public void store(byte[] nodeId, byte[] key, byte[] value) {
        //todo
    }

}
