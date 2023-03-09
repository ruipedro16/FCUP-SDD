package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class Bucket {
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

    public void insertNode(@NonNull Node node) {
        // todo
    }

    public void removeNode(@NonNull Node node) {
        nodes.remove(node);
    }

    public boolean isFull() {
        return nodes.size() == BUCKET_SIZE;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
