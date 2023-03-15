package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoutingTable {
    /*
    protected static final int N_BUCKETS = KademliaConstants.K;

    private final byte[] currentNodeID;
    private final List<Bucket> buckets;

    public RoutingTable(byte[] currentNodeID) {
        this.currentNodeID = currentNodeID;
        this.buckets = new ArrayList<>();

        for (int i = 0; i < N_BUCKETS; i++) {
            buckets.add(new Bucket(currentNodeID, i));
        }
    }

    public void insertNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).insertNode(node);
    }

    public void removeNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).removeNode(node);
    }

    public List<Node> getAllNodes() {
        return buckets.stream()
                .flatMap(bucket -> bucket.getNodes().stream())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < N_BUCKETS; i++) {
            if (!buckets.get(i).isEmpty()) {
                sb.append("Bucket: ").append(i).append('\n');
                for (Node n : buckets.get(i).getNodes()) {
                    sb.append(n.toString()).append('\n');
                }
            }
        }

        return sb.toString();
    }
     */
}
