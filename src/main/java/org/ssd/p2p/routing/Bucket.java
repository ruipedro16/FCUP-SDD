package org.ssd.p2p.routing;

import lombok.NonNull;
import org.ssd.constants.KademliaConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Bucket {

    private final Deque<NodeContact> contacts;
    private final Deque<NodeContact> replacementCache;

    public Bucket() {
        this.contacts = new ConcurrentLinkedDeque<>(); // thread-safe implementation of a deque that allows concurrent access to the queue without the need for explicit synchronization.
        this.replacementCache = new ConcurrentLinkedDeque<>(); // thread-safe implementation of a deque that allows concurrent access to the queue without the need for explicit synchronization.
    }

    public List<NodeContact> getAllContacts() {
        return new ArrayList<>(this.contacts);
    }

    private boolean isFull() {
        return this.contacts.size() == KademliaConstants.K;
    }

    public boolean isEmpty() {
        return this.contacts.isEmpty();
    }

    public synchronized void insertContact(@NonNull NodeContact nodeContact) {
        // If the contact already exists in the bucket, we move it to the tail of the bucket
        if (this.contacts.contains(nodeContact)) {
            this.contacts.remove(nodeContact);
            nodeContact.setLastSeen(System.currentTimeMillis());
            nodeContact.resetStaleNodesCount();
            this.contacts.addLast(nodeContact);
        } else {
            /*
             * If the k-bucket has fewer than k entries - i.e. is not full - and the sender is not already in the k-bucket,
             * the sender is inserted at the tail of the list.
             */
            if (!this.isFull()) {
                this.contacts.addLast(nodeContact);
            } else {
                /*
                 * If the k-bucket is full, we add the new node into the replacement cache.
                 */
                insertIntoReplacementCache(nodeContact);

                /*
                 * We then check for stale nodes -- nodes that have not been responsive or active for a period of time.
                 * If there are any stale nodes, we replace the stalest with the first contact in the replacement cache.
                 */
                NodeContact stalest = contacts.stream()
                        .filter(n -> n.getStaleCount() >= KademliaConstants.STALENESS_LIMIT)
                        .max(Comparator.comparingInt(NodeContact::getStaleCount))
                        .orElse(null);

                if (stalest != null) {
                    this.contacts.remove(stalest);
                    this.contacts.addLast(replacementCache.removeFirst());
                }
            }
        }
    }

    private synchronized void insertIntoReplacementCache(@NonNull NodeContact nodeContact) {
        /*
         * If the contact is already in the replacement cache, we move it to the tail of the list
         */
        if (this.replacementCache.contains(nodeContact)) {
            this.replacementCache.remove(nodeContact);
            nodeContact.setLastSeen(System.currentTimeMillis());
            nodeContact.resetStaleNodesCount();
            this.replacementCache.addLast(nodeContact);
        } else {
            /*
             * If the replacement cache is not null, we add the contact to the tail of the list
             */
            if (this.replacementCache.size() < KademliaConstants.K) {
                replacementCache.addLast(nodeContact);
            }
        }
    }

    public synchronized void removeContact(@NonNull NodeContact nodeContact) {
        /*
         * If the k-bucket is not full or its replacement cache is empty, we do not remove stale contacts but flags
         * accordingly. This ensures that if a node's network connection goes down temporarily, the node will not
         * completely empty all of its k-buckets.
         */
        if (this.contacts.contains(nodeContact)) {
            if (this.replacementCache.isEmpty() || this.contacts.size() < KademliaConstants.K) {
                this.contacts.stream()
                        .filter(c -> c.equals(nodeContact))
                        .forEach(NodeContact::incrementStaleNodesCount);
            } else {
                this.contacts.remove(nodeContact);
                this.contacts.addLast(this.replacementCache.removeFirst());
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        this.contacts.forEach(sb::append);
        sb.append('\n');
        return sb.toString();
    }
}
