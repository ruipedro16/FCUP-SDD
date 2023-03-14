package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


@Data
public class Bucket {
    private static final Logger logger = LogManager.getLogger(Bucket.class);
    protected static final int BUCKET_SIZE = 1; // todo: change this

    private final byte[] currentNodeID;
    private final List<Node> nodes;
    private final int bucketIndex;

    public Bucket(byte[] currentNodeID, int bucketIndex) {
        this.currentNodeID = currentNodeID;
        this.bucketIndex = bucketIndex;
        this.nodes = new ArrayList<>();
    }

    public int size() {
        return nodes.size();
    }

    /*
     * If the node is already in the list, we remove it and re-add it
     * If list is full, we ping oldest node. If it answers, ...
     */
    public void insertNode(@NonNull Node node) {
        ListIterator<Node> it = (ListIterator<Node>) this.nodes.iterator();
        while (it.hasNext()) {
            Node itNode = it.next();
            if (itNode.getAddress().toString().equals(node.getAddress().toString()) &&
            itNode.getPort() == node.getPort()) {
                it.remove();
                it.add(node);
            }
        }

        if (nodes.size() == BUCKET_SIZE) {
            // ping the oldest node
            Node oldestNode = nodes.get(0);

            byte[] response = null; // TODO: response of pinging the node
            if (response != null) {
                this.nodes.remove(this.nodes.get(0));
                // TODO: re add the node
            }
            else {
                this.nodes.remove(this.nodes.get(0));
                this.nodes.add(node);
            }
         } else {
            this.nodes.add(node);
        }

        logger.debug("Inserted node: " + Hex.toHexString(node.getId()));
    }

    public void removeNode(@NonNull Node node) {
        nodes.remove(node);
        logger.debug("Removed node: " + Hex.toHexString(node.getId()));
    }

    public boolean isFull() {
        return nodes.size() == BUCKET_SIZE;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
