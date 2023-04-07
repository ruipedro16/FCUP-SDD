package org.ssd.p2p;

import lombok.Data;
import org.ssd.constants.KademliaConstants;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;
import org.ssd.p2p.grpc.GrpcKadStubManager;
import org.ssd.p2p.grpc.GrpcStubRouter;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Pair;

import java.util.Arrays;

@Data
public class Node {
    private final byte[] id;
    private final int port;
    private final InetAddress address;
    private long seen;
    private RoutingTable routingTable;
    private Storage storage;

    /*
     * This constructor will never be used because the ID needs to be generated from a challenge to prevent Sybil attacks
     * Node constructor
     *
     * @param id   node id
     * @param port node port
    public Node(byte[] id, int port, GrpcStubRouter stubRouter, GrpcKadStubManager kadStubManager) {
        this.id = id;
        this.port = port;
        //init k_buckets
        this.routingTable = new RoutingTable(id, stubRouter, kadStubManager); // initializes the routing table & k buckets
        //init the rest
        this.address = null; // todo: change this
    }
    */

    /**
     * Constructor with omitted nodeId. It generates a random one.
     *
     * @param port node port
     */
    public Node(int port, GrpcStubRouter stubRouter, GrpcKadStubManager kadStubManager) {
        /*
        byte[] genId = new byte[KademliaConstants.B];
        random.nextBytes(genId);
         */
        this.id = generateID();
        this.port = port;
        this.routingTable = new RoutingTable(this.id, stubRouter, kadStubManager); // initializes the routing table & k buckets
        this.address = null; // todo: change this
    }

    /*
     * Generates the node id by solving a challenge
     * Similar to PoW
     */
    private byte[] generateID() {
        Random random = new SecureRandom();
        byte[] res = new byte[KademliaConstants.B];
        random.nextBytes(res);

        long nonce = 0L;
        byte[] dataToHash = null;

        String target = new String(new char[KademliaConstants.PREFIX_LENGTH]).replace('\0', '0');
        while(!Hex.toHexString(this.id).substring(0, KademliaConstants.PREFIX_LENGTH).equals(target)) {
            // increment the nonce
            nonce++;
            res = CryptoUtils.hash(dataToHash);
        }

        return res;
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
     * Private call wrapper to RPC ping
     * @param contact contact to ping (must have nodeId, INetAddress and port at least)
     */
    private void ping(NodeContact contact) {
        this.routingTable.getKadStubRouter().ping(contact, this, this.routingTable.getStubRouter());
    }

    /**
     * Private call wrapper to RPC store
     * @param target target to store data to
     * @param dataOwnerId data owner from where data originated from
     * @param key key of the data
     * @param dataValue value to store
     */
    private void store(NodeContact target,byte[] dataOwnerId ,byte[] key,byte[] dataValue) {
        this.routingTable.getKadStubRouter().store(target, this,dataOwnerId,key,dataValue, this.routingTable.getStubRouter());
    }

    /**
     * Set a node triple address as seen after. for example, a ping
     * @param target Target to set as seen by this node
     */
    public void setNodeAsSeen(NodeContact target) {
        int kBucketIdx = getBucket(this.getId(), target.getId());
        //get bucket
        Bucket bucket = this.routingTable.getBuckets().get(kBucketIdx);
        if (bucket.getContacts() == null) {
            bucket.setContacts(new ArrayList<>());
        }

        target.setSeen(System.currentTimeMillis());

        if (bucket.containsNode(target)) {
            //move to tail of bucket
            bucket.moveToTail(target);
        } else {
            // challenge to prevent sybil
            //todo

            if (!bucket.isFull()) {
                //add if it has space
                bucket.moveToTail(target);
            } else {
                //if size exceeds
                //ping least recently seen (the bucket is ordered from oldest to most recent), the head of bucket
                NodeContact headContact = bucket.getContacts().get(0);
                this.ping(headContact);
                //if it is alive, discard this target
                //else remove head and add target to the end
            }
        }
        //place bucket in position after mutation
        this.routingTable.putKBucketAtPosition(kBucketIdx, bucket);
    }

    /**
     * Store a given message to this node and propagate to nearby k nodes
     * @param dataOwnerId id of the node that requested the store
     * @param key id key for the message
     * @param value value/message to store
     */
    public void storeInNode(byte[] dataOwnerId, byte[] key, byte[] value) {
        //if the data owner is us
        if (Arrays.equals(dataOwnerId, this.getId())) {
            //store locally
            this.storage.addValueToKey(new Pair<>(dataOwnerId, key), value);
        }
        if (this.storage.hasKey(new Pair<>(dataOwnerId, key))) {
            this.storage.addValueToKey(new Pair<>(dataOwnerId, key), value);
        } else {
            // TODO:
            //get best k_bucket
            //iterate through all (Bucket.containsNode)
            //if found -> store locally on target node
            //if no found -> send find_node to all in k_bucket
        }
    }

}
