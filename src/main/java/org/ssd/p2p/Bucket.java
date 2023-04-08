package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.constants.KademliaConstants;

import java.util.Arrays;
import java.util.List;


@Data
public class Bucket {
    /*
     * Contains information about each node
     * byte[]      -> Node ID
     * InetAddress -> Address of the node
     * Integer     -> port
     */
    private List<NodeContact> contacts;

    public Bucket() {
        //todo
    }

    public boolean isFull() {
        return this.contacts.size() == KademliaConstants.K;
    }

    public boolean isEmpty() { return this.contacts.size() == 0; }

    public int size() {
        return this.contacts.size();
    }

    public boolean addNode(@NonNull NodeContact node) {
        if (this.getContacts().size() < KademliaConstants.K) {
            return this.contacts.add(node);
        }
        return false;
    }

    public boolean containsAndMoveToTail(@NonNull NodeContact node) {
        boolean found = false;

        NodeContact tmp = null;

        for (NodeContact n : this.contacts) {
            if (Arrays.equals(n.getId(), node.getId())) { // compare the IDs of the nodes
                tmp = n;
                found = true;
            }
        }

        if (found) { // if the node is in the k bucket we remove it and re add it
            this.moveToTail(node);
        }

        return found;
    }

    /**
     * Simple contains method, does not mutate array
     * @param node node to search
     * @return boolean
     */
    public boolean containsNode(@NonNull NodeContact node) {
        boolean found = false;

        NodeContact tmp = null;

        for (NodeContact n : this.contacts) {
            if (Arrays.equals(n.getId(), node.getId())) { // compare the IDs of the nodes
                found = true;
                break;
            }
        }

        return found;
    }

    /**
     * Returns index of node to search
     * @param node node to get the index of
     * @return -1 if it doesn't exist, any other is the index
     */
    public int indexOfNode(@NonNull NodeContact node) {
        boolean exists = false; int tripleIdx = 0;
        for (NodeContact t : getContacts()) {
            if (Arrays.equals(node.getId(), t.getId())) {
                //found
                exists = true;
                break;
            }
            tripleIdx++;
        }

        if (!exists) {
            tripleIdx = -1;
        }

        return tripleIdx;
    }

    public boolean removeNode(@NonNull NodeContact node) {
        return this.getContacts().remove(node);
    }

    public NodeContact removeNodeById(int i) {
        return this.getContacts().remove(i);
    }

    public boolean moveIdxToTail(int i) {
        NodeContact toMove = this.removeNodeById(i);
        return this.addNode(toMove);
    }

    public boolean moveToTail(@NonNull NodeContact node) {
        boolean res = this.removeNode(node);
        if (res) {
            return this.addNode(node);
        }
        return false;
    }
}
