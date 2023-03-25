package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.constants.KademliaConstants;
import org.ssd.utils.Triple;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;


@Data
public class Bucket {
    private static final Logger logger = LogManager.getLogger(Bucket.class);

    /*
     * Contains information about each node
     * byte[]      -> Node ID
     * InetAddress -> Address of the node
     * Integer     -> port
     */
    private List<Triple<byte[], InetAddress, Integer>> contacts;

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

    public void addNode(@NonNull Triple<byte[], InetAddress, Integer> node) {
        this.contacts.add(node);
    }

    public boolean containsNode(@NonNull Triple<byte[], InetAddress, Integer> node) {
        boolean found = false;

        Triple<byte[], InetAddress, Integer> tmp = null;

        for (Triple<byte[], InetAddress, Integer> n : this.contacts) {
            if (Arrays.equals(n.getFirst(), node.getFirst())) { // compare the IDs of the nodes
                tmp = n;
                found = true;
            }
        }

        if (found) { // if the node is in the k bucket we remove it and re add it
            this.contacts.remove(tmp);
            this.contacts.add(tmp);
        }

        return found;
    }


    public void removeNode(@NonNull Triple<byte[], InetAddress, Integer> node) {
        this.contacts.remove(node);
    }

}
