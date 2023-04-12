package org.ssd.p2p;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.grpc.GrpcKadStubManager;
import org.ssd.p2p.grpc.GrpcStubRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoutingTable {

    protected static final int N_BUCKETS = KademliaConstants.K;

    private final byte[] currentNodeID;
    private final List<Bucket> buckets;
    private final GrpcStubRouter stubRouter; // used to manage the gRPC connections to other nodes in the network
    private final GrpcKadStubManager kadStubRouter; // used to manage the gRPC connections to other nodes in the network

    public RoutingTable(byte[] currentNodeID, @NonNull GrpcStubRouter stubRouter, @NonNull GrpcKadStubManager kadStubRouter) {
        this.currentNodeID = currentNodeID;
        this.stubRouter = stubRouter;
        this.kadStubRouter = kadStubRouter;
        this.buckets = new ArrayList<>();

        for (int i = 0; i < N_BUCKETS; i++) {
            buckets.add(i, new Bucket());
        }
    }

    public Bucket getBucket(int index) {
        return this.buckets.get(index);
    }

    public void insertNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).addNode(new NodeContact(node.getId(), node.getAddress(), node.getPort()));
    }

    public void insertNode(@NonNull NodeContact node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).addNode(new NodeContact(node.getId(), node.getAddress(), node.getPort()));
    }

    public void removeNode(@NonNull Node node) {
        int index = Node.getBucket(currentNodeID, node.getId());
        buckets.get(index).removeNode(new NodeContact(node.getId(), node.getAddress(), node.getPort()));
    }

    public List<NodeContact> getAllNodes() {
        return buckets.stream()
                .flatMap(bucket -> bucket.getContacts().stream())
                .collect(Collectors.toList());
    }

    public void putKBucketAtPosition(int index, @NonNull Bucket toAdd) {
        //NOTE: .set() replaces. See .add() for a different method
        this.getBuckets().set(index, toAdd);
    }

    public List<NodeContact> getKClosestNodes(byte[] targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }
        
        // TODO:
        return null;
    }
}
