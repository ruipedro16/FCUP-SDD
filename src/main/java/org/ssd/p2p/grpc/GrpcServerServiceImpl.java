package org.ssd.p2p.grpc;

import io.grpc.stub.StreamObserver;
import org.ssd.*;
import org.ssd.p2p.Node;

/**
 * Protobuf service implementation
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
        super.ping(request, responseObserver);
    }

    @Override
    public void store(Store request, StreamObserver<Store> responseObserver) {
        super.store(request, responseObserver);
    }

    @Override
    public void findNode(FindNodeRequest request, StreamObserver<FindNodeResponse> responseObserver) {
        super.findNode(request, responseObserver);
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
