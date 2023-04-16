package org.ssd.p2p.routing;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;

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
        for (int i = 0; i < KademliaConstants.K; i++) {
            this.buckets.add(i, new Bucket());
        }
        this.addContact(currentNode);
    }

    private int getBucketIndex(@NonNull NodeContact nodeContact) {
        return Node.getBucket(this.currentNode.getId(), nodeContact.getId());
    }

    public synchronized void addContact(@NonNull NodeContact contact) {
        int bucketIndex = getBucketIndex(contact);
        System.out.println(bucketIndex);
        assert bucketIndex >= 0 && bucketIndex <= KademliaConstants.K;
        this.buckets.get(bucketIndex).insertContact(contact);
    }

    public Bucket getBucketAtIndex(int index) {
        if (index < 0 || index >= KademliaConstants.K) {
            throw new IndexOutOfBoundsException();
        }
        return this.buckets.get(index);
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

        int n = KademliaConstants.K;

        Set<NodeContact> sortedContacts = new TreeSet<>(new NodeContactDistanceComparator(targetID));
        sortedContacts.addAll(this.getAllNodes()); // TODO: remove this
        int bucketIndex = Node.getBucket(this.currentNode.getId(), targetID);

        /*
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
            if (sortedContacts.size() > n || !lookBefore && !lookAfter) {
                break;
            }
        }
        */
        return sortedContacts.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    public void warnUnresponsiveContact(@NonNull NodeContact nodeContact) {
        int bucketIndex = getBucketIndex(nodeContact);
        this.buckets.get(bucketIndex).removeContact(nodeContact);
    }

    public void warnUnresponsiveContacts(@NonNull List<NodeContact> nodeContacts) {
        nodeContacts.forEach(this::warnUnresponsiveContact);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        this.buckets.forEach(sb::append);
        return sb.toString();
    }
}
