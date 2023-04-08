package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.grpc.GrpcKadStubManager;
import org.ssd.p2p.grpc.GrpcStubRouter;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Pair;
import org.ssd.utils.Utils;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.*;

@Data
public class Node {
    private final byte[] id;
    private final int port;
    private final InetAddress address;
    private long seen;
    private RoutingTable routingTable;
    private Storage storage;

    /**
     * Constructor with omitted nodeId. It generates a random one.
     *
     * @param port node port
     */
    public Node(int port, @NonNull GrpcStubRouter stubRouter, @NonNull GrpcKadStubManager kadStubManager) {
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
        while (!Hex.toHexString(this.id).substring(0, KademliaConstants.PREFIX_LENGTH).equals(target)) {
            // increment the nonce
            nonce++;
            res = CryptoUtils.hash(dataToHash);
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

    public static byte[] getDistance(byte[] id1, byte[] id2) {
        if (id1 == null || id2 == null || id1.length != KademliaConstants.B || id2.length != KademliaConstants.B) {
            throw new IllegalArgumentException();
        }

        byte[] res = new byte[id1.length];

        for (int i = 0; i < id1.length; i++) {
            res[i] = (byte) (id1[i] ^ id2[i]);
        }

        return res;
    }

    /*
     * Given two node IDs,
     */
    public static int getBucket(byte[] currentNodeId, byte[] other) {
        byte[] distance = getDistance(currentNodeId, other);
        return getPrefixLength(distance);
    }

    /**
     * Function receives the necessary info to place this node into a kademlia network
     */
    public void init() {
        //todo
    }

    /**
     * Private call wrapper to RPC ping
     *
     * @param contact contact to ping (must have nodeId, INetAddress and port at least)
     */
    private void ping(@NonNull NodeContact contact) {
        this.routingTable.getKadStubRouter().ping(contact, this, this.routingTable.getStubRouter());
    }

    /**
     * Private call wrapper to RPC store
     *
     * @param target      target to store data to
     * @param dataOwnerId data owner from where data originated from
     * @param key         key of the data
     * @param dataValue   value to store
     */
    private void store(NodeContact target, byte[] dataOwnerId, byte[] key, byte[] dataValue) {
        this.routingTable.getKadStubRouter().store(target, this, dataOwnerId, key, dataValue, this.routingTable.getStubRouter());
    }

    /**
     * Set a node triple address as seen after. for example, a ping
     * <p>
     * updates the routing table of the node to indicate that it has seen a particular node.
     * It either moves the node to the end of its corresponding bucket (if it is already in the bucket), or adds it to
     * the bucket (if the bucket is not full) or pings the head of the bucket (if the bucket is full).
     *
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
            bucket.moveToTail(target); //move to tail of bucket
        } else {
            // challenge to prevent sybil
            //todo

            if (!bucket.isFull()) {
                bucket.moveToTail(target); //add if it has space
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
     *
     * @param dataOwnerId id of the node that requested the store
     * @param key         id key for the message
     * @param value       value/message to store
     */
    public void storeInNode(byte[] dataOwnerId, byte[] key, byte[] value) {
        //if the data owner is us, store locally
        if (Arrays.equals(dataOwnerId, this.getId())) {
            this.storage.addValueToKey(new Pair<>(dataOwnerId, key), value);
        }

        if (this.storage.hasKey(new Pair<>(dataOwnerId, key))) {
            this.storage.addValueToKey(new Pair<>(dataOwnerId, key), value);
        } else {
            // get the best k-bucket for the given key
            int kBucketIdx = getBucket(this.getId(), key); // get the best k-bucket for the given key
            Bucket bucket = this.routingTable.getBuckets().get(kBucketIdx);

            // Iterate through all nodes and check if any node in the k-bucket contains the key
            boolean found = false;
            for (NodeContact contact : bucket.getContacts()) {
                // check if the contact contains the key
                // this.routingTable.getKadStubRouter().findValue(contact, key, this.getRoutingTable().getStubRouter());
                if (this.storage.hasKey(new Pair<>(dataOwnerId, key))) {
                    // if found, store the message locally on the contact node and propagate it to nearby nodes
                    this.routingTable.getKadStubRouter().store(contact, this, dataOwnerId, key, value, this.routingTable.getStubRouter());
                    found = true;
                    break;
                }
            }

            if (!found) {
                // if not found, send a FIND_NODE message to all nodes in the k-bucket
                bucket.getContacts()
                        .forEach(contact -> routingTable.getKadStubRouter().findNode(contact, this, this.routingTable.getStubRouter()));
            }
        }

    }

    /**
     * Find the K nodes in the network that are closest to a given key
     *
     * @param key
     * @return
     */
    public List<NodeContact> findClosestNodes(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        List<NodeContact> closestNodes = new ArrayList<>();
        int kBucketIdx = getBucket(this.getId(), key); // index of the Kademlia bucket that contains the key

        Bucket kBucket = this.routingTable.getBuckets().get(kBucketIdx);
        if (kBucket.getContacts() != null) {
            closestNodes.addAll(kBucket.getContacts());
        }

        // Iterate through the buckets to the left and right of the key's bucket, adding the contacts from those buckets
        // to the closestNodes list as well, until the list contains K nodes or there are no more buckets to add.
        for (int i = 1; closestNodes.size() < KademliaConstants.K && (kBucketIdx - i >= 0 || kBucketIdx + i < KademliaConstants.B); i++) {
            if (kBucketIdx - i >= 0) {
                Bucket leftBucket = this.routingTable.getBuckets().get(kBucketIdx - i);
                if (leftBucket.getContacts() != null) {
                    closestNodes.addAll(leftBucket.getContacts());
                }
            }

            if (kBucketIdx + i < KademliaConstants.B) {
                Bucket rightBucket = this.routingTable.getBuckets().get(kBucketIdx + i);
                if (rightBucket.getContacts() != null) {
                    closestNodes.addAll(rightBucket.getContacts());
                }
            }
        }

        // sorted by distance from the key
        closestNodes.sort(new NodeContactDistanceComparator(key));

        // Iterate through the closestNodes list and call findNode on each node, except for the current node. This is
        // to update the current node's routing table with the most up-to-date information about the network.
        closestNodes = closestNodes.subList(0, Math.min(KademliaConstants.K, closestNodes.size()));
        for (NodeContact node : closestNodes) {
            if (!java.util.Arrays.equals(node.getId(), this.getId())) {
                this.routingTable.getKadStubRouter().findNode(node, this, this.routingTable.getStubRouter());
            }
        }

        return closestNodes;
    }
}
