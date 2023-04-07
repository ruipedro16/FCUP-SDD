package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.grpc.GrpcKadStubManager;
import org.ssd.p2p.grpc.GrpcStubRouter;
import org.ssd.utils.NodeContact;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoutingTable {

    protected static final int N_BUCKETS = KademliaConstants.K;
    private final byte[] currentNodeID;
    private final List<Bucket> buckets;
    private final GrpcStubRouter stubRouter;
    private final GrpcKadStubManager kadStubRouter;

    public RoutingTable(byte[] currentNodeID, GrpcStubRouter stubRouter, GrpcKadStubManager kadStubRouter) {
        this.currentNodeID = currentNodeID;
        this.stubRouter = stubRouter;
        this.kadStubRouter = kadStubRouter;
        this.buckets = new ArrayList<>();

        for (int i = 0; i < N_BUCKETS; i++) {
            buckets.add(i, new Bucket());
        }
    }

    public void insertNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).addNode(new NodeContact(node.getId(),node.getAddress(), node.getPort()));
    }

    public void removeNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).removeNode(new NodeContact(node.getId(),node.getAddress(), node.getPort()));
    }

    public List<NodeContact> getAllNodes() {
        return buckets.stream()
                .flatMap(bucket -> bucket.getContacts().stream())
                .collect(Collectors.toList());
    }

    public void putKBucketAtPosition(int index, Bucket toAdd) {
        //.set() replaces. See .add() for a different method
        this.getBuckets().set(index, toAdd);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < N_BUCKETS; i++) {
            if (!buckets.get(i).isEmpty()) {
                sb.append("Bucket: ").append(i).append('\n');
                for (NodeContact n : buckets.get(i).getContacts()) {
                    sb.append(n.toString()).append('\n');
                }
            }
        }

        return sb.toString();
    }

}
