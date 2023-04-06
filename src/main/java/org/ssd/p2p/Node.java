package org.ssd.p2p;

import lombok.Data;
import org.ssd.constants.KademliaConstants;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;
import org.ssd.p2p.grpc.GrpcKadStubManager;
import org.ssd.p2p.grpc.GrpcStubRouter;
import org.ssd.utils.NodeContact;

import java.util.Arrays;

@Data
public class Node {

    private static final Random random = new Random();
    private final byte[] id;
    private final int port;
    private final InetAddress address;
    private long seen;
    private RoutingTable routingTable;

    /**
     * Node constructor
     *
     * @param id   node id
     * @param port node port
     */
    public Node(byte[] id, int port, GrpcStubRouter stubRouter, GrpcKadStubManager kadStubManager) {
        this.id = id;
        this.port = port;
        //init k_buckets
        this.routingTable = new RoutingTable(id, stubRouter, kadStubManager); // initializes the routing table & k buckets
        //init the rest
        this.address = null; // todo: change this
    }

    /**
     * Constructor with omitted nodeId. It generates a random one.
     *
     * @param port node port
     */
    public Node(int port, GrpcStubRouter stubRouter, GrpcKadStubManager kadStubManager) {
        byte[] genId = new byte[KademliaConstants.B];
        random.nextBytes(genId);
        this.id = genId;
        this.port = port;
        this.routingTable = new RoutingTable(genId, stubRouter, kadStubManager); // initializes the routing table & k buckets
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

    /**
     * Set a node triple address as seen after. for example, a ping
     * @param target
     */
    // TODO: Use routing table methods instead of adds, getters and remove here
    public void setNodeAsSeen(NodeContact target) {
        int kBucketIdx = getBucket(this.getId(), target.getId());
        //get bucket
        Bucket bucket = this.routingTable.getBuckets().get(kBucketIdx);
        if (bucket.getContacts() == null) {
            bucket.setContacts(new ArrayList<>());
        }

        target.setSeen(System.currentTimeMillis());
        //iterate through all in bucket// TODO: use containsNode but with a idx returnable
        boolean exists = false; int tripleIdx = 0;
        for (NodeContact t : bucket.getContacts()) {
            if (Arrays.equals(target.getId(), t.getId())) {
                //found
                exists = true;
                break;
            }
            tripleIdx++;
        }

        if (exists) {
            //move to tail of bucket
            bucket.moveIdxToTail(tripleIdx);
        } else {
            // challenge to prevent sybil
            //todo

            if (bucket.getContacts().size() <= KademliaConstants.K) {
                //add if has space
                bucket.getContacts().add(target);//todo: use routing table methods
            } else {
                //if size exceeds
                //ping least recently seen (the bucket is ordered from oldest to most recent), the head of bucket
                NodeContact headContact = bucket.getContacts().get(0);
                this.routingTable.getKadStubRouter().ping(headContact, this, this.routingTable.getStubRouter());
                //if it is alive, discard this target
                //else remove head and add target to the end
            }
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
