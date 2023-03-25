package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.utils.Triple;

import java.net.InetAddress;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoutingTable {

    protected static final int N_BUCKETS = KademliaConstants.K;
    private final byte[] currentNodeID;
    private final List<Bucket> buckets;

    public RoutingTable(byte[] currentNodeID) {
        this.currentNodeID = currentNodeID;
        this.buckets = new ArrayList<>();

        for (int i = 0; i < N_BUCKETS; i++) {
            buckets.add(i, new Bucket());
        }
    }

    public void insertNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).addNode(new Triple<>(node.getId(),node.getAddress(), node.getPort()));
    }

    public void removeNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).removeNode(new Triple<>(node.getId(),node.getAddress(), node.getPort()));
    }

    public List<Triple<byte[], InetAddress, Integer>> getAllNodes() {
        return buckets.stream()
                .flatMap(bucket -> bucket.getContacts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < N_BUCKETS; i++) {
            if (!buckets.get(i).isEmpty()) {
                sb.append("Bucket: ").append(i).append('\n');
                for (Triple<byte[], InetAddress, Integer> n : buckets.get(i).getContacts()) {
                    sb.append(n.toString()).append('\n');
                }
            }
        }

        return sb.toString();
    }

}
