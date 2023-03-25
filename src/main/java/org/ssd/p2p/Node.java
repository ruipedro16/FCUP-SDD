package org.ssd.p2p;

import lombok.Data;
import org.ssd.constants.KademliaConstants;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;
import org.ssd.utils.Triple;

import java.util.Arrays;

@Data
public class Node {

    private static final Random random = new Random();
    private final byte[] id;
    private final int port;
    private final InetAddress address;
    private long seen;
    //add k_buckets here or in helper RoutingTable
    //private List<Bucket> kBucketList;
    private RoutingTable routingTable;

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

    public void setNodeAsSeen(Triple<byte[], InetAddress, Integer> target) {
        int kBucketIdx = getBucket(this.getId(), target.getFirst());
        //get bucket
        Bucket bucket = this.routingTable.getBuckets().get(kBucketIdx);
        if (bucket.getContacts() == null) {
            bucket.setContacts(new ArrayList<Triple<byte[], InetAddress, Integer>>());
        }

        // target.setSeen(System.currentTimeMillis()); todo: either Triple becomes its own class or a node should instanced any time a contact is saved in bucket
        //iterate through all in bucket
        boolean exists = false;
        for (Triple<byte[], InetAddress, Integer> t : bucket.getContacts()) {
            if (Arrays.equals(target.getFirst(), t.getFirst())) {
                //found
                exists = true;
                break;
            }
        }

        if (exists) {
            //move to tail of bucket
        } else {
            // not present, so we start the full ping process (w/ challenge)
        }
    }

    public boolean ping(byte[] nodeId, InetAddress address, int port) {
        return true;
    }

    /**
     * Store a given message to this node and propagate to nearby k nodes
     * @param nodeId id of the node that requested the store
     * @param key id key for the message
     * @param value value/message to store
     */
    public void store(byte[] nodeId, byte[] key, byte[] value) {
        //get nearest nodes
        if (Arrays.equals(nodeId, this.getId())) {
            //store locally or extract this condition
        } else {
            //get best k_bucket
            //iterate through all (Bucket.containsNode)
            //if found -> store locally on target node
            //if no found -> send find_node to all in k_bucket
        }
    }

}
