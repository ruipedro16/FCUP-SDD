package org.ssd.p2p.routing;

import com.google.common.math.BigIntegerMath;
import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Data
public class RoutingTable {
    private final NodeContact currentNode;
    private final List<Bucket> buckets;

    public RoutingTable(@NonNull NodeContact currentNode) {
        this.currentNode = currentNode;
        this.buckets = new ArrayList<>();
        for (int i = 0; i < KademliaConstants.B; i++) {
            this.buckets.add(i, new Bucket());
        }
        this.addContact(currentNode);
    }

    private int getBucketIndex(@NonNull NodeContact nodeContact) {
        byte[] targetID = nodeContact.getId();

        // Compute the XOR distance
        BigInteger b1 = new BigInteger(1, this.currentNode.getId());
        BigInteger b2 = new BigInteger(1, targetID);
        BigInteger distance = b1.xor(b2);

        if (distance.equals(BigInteger.ZERO)) {
            return 0;
        } else {
            return BigIntegerMath.log2(distance, RoundingMode.DOWN);
        }
    }

    private int getBucketIndex(byte[] targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }

        // Compute the XOR distance
        BigInteger b1 = new BigInteger(1, this.currentNode.getId());
        BigInteger b2 = new BigInteger(1, targetID);
        BigInteger distance = b1.xor(b2);

        if (distance.equals(BigInteger.ZERO)) {
            return 0;
        } else {
            return BigIntegerMath.log2(distance, RoundingMode.DOWN);
        }
    }

    public synchronized void addContact(@NonNull NodeContact contact) {
        int bucketIndex = getBucketIndex(contact);
        this.buckets.get(bucketIndex).insertContact(contact);
    }

    public List<NodeContact> getAllNodes() {
        return buckets.stream()
                .flatMap(bucket -> bucket.getAllContacts().stream())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of the K closest NodeContacts to the specified target ID, sorted by distance in ascending order.
     *
     * @param targetID the ID to search for the closest nodes to
     * @return a list of the n closest NodeContacts to the target ID
     */
    public synchronized List<NodeContact> getKClosestNodes(byte[] targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }

        Set<NodeContact> sortedContacts = new TreeSet<>(new NodeContactDistanceComparator(targetID));
        // sortedContacts.addAll(this.getAllNodes()); // TODO: remove this
        int bucketIndex = getBucketIndex(targetID);

        for (int i = 0; i < KademliaConstants.B; i++) {
            boolean lookBefore = bucketIndex - i >= 0;
            boolean lookAfter = bucketIndex + i < KademliaConstants.B;

            // Add all the NodeContacts in the k-bucket before the current one
            if (lookBefore) {
                sortedContacts.addAll(this.buckets.get(bucketIndex - i).getAllContacts());
            }

            // Add all the NodeContacts in the k-bucket after the current one
            if (lookAfter && i > 0) {
                sortedContacts.addAll(this.buckets.get(bucketIndex + i).getAllContacts());
            }

            // Break out of the loop if we have found the required number of contacts, or if we have searched all relevant k-buckets
            if (sortedContacts.size() > KademliaConstants.K || !lookBefore && !lookAfter) {
                break;
            }
        }

        return sortedContacts.stream()
                .limit(KademliaConstants.K)
                .collect(Collectors.toList());
    }

    public void warnUnresponsiveContact(@NonNull NodeContact nodeContact) {
        int bucketIndex = getBucketIndex(nodeContact);
        this.buckets.get(bucketIndex).removeContact(nodeContact);
    }

    public String toStringOmitEmpty() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.buckets.size(); i++) {
            if (!this.buckets.get(i).isEmpty()) {
                sb.append("Bucket[").append(i).append("]\n");
                sb.append(this.buckets.get(i).toString());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.buckets.size(); i++) {
            sb.append("Bucket[").append(i).append("]\n");
            if (!this.buckets.get(i).isEmpty()) {
                sb.append(this.buckets.get(i).toString());
            }
        }
        return sb.toString();
    }
}
