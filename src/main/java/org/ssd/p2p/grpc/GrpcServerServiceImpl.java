package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.ssd.*;
import org.ssd.p2p.Node;

/**
 * Protobuf service implementation for receiving proto messages
 */
public class GrpcServerServiceImpl extends P2PGrpcServiceGrpc.P2PGrpcServiceImplBase {

    private Node node;

    //add message subscribers

    // todo
    public GrpcServerServiceImpl(Node node) {
        this.node = node;
    }

    @Override
    public void ping(Ping request, StreamObserver<Ping> responseObserver) {
        Ping.Builder pingResponse = Ping.newBuilder();
        pingResponse.setNodeId(ByteString.copyFrom(this.node.getId()));
        pingResponse.setReqNodePort(this.node.getPort()); // return this node's port unless we want to return another message
        responseObserver.onNext(pingResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void store(Store request, StreamObserver<Store> responseObserver) {
        byte[] nodeId = request.getNodeId().toByteArray();
        byte[] key = request.getKey().toByteArray();
        byte[] value = request.getValue().toByteArray();
        this.node.store(nodeId, key, value);
        Store.Builder storeResponse = Store.newBuilder();
        storeResponse.setValue(request.getValue());
        responseObserver.onNext(storeResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findNode(FindNodeRequest request, StreamObserver<FindNodeResponse> responseObserver) {
        //find node in routing table, add this.node.findNodes
        responseObserver.onCompleted();
    }

    @Override
    public void findValue(FindValueRequest request, StreamObserver<FindValueResponse> responseObserver) {
        super.findValue(request, responseObserver);
    }

    @Override
    public void broadcastMessage(Message request, StreamObserver<Empty> responseObserver) {
        super.broadcastMessage(request, responseObserver);
    }

    @Override
    public void sendMessage(Message request, StreamObserver<Empty> responseObserver) {
        super.sendMessage(request, responseObserver);
    }
}
