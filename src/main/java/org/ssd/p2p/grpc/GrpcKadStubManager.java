package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.*;
import org.ssd.p2p.Node;
import org.ssd.p2p.NodeContact;

/**
 * Kademlia messages (only) stub builder and handler to send messages
 */
public class GrpcKadStubManager {

    private static GrpcKadStubManager instance;

    private GrpcKadStubManager() {
        instance = this;
    }

    public static GrpcKadStubManager getInstance() {
        if (instance == null) {
            instance = new GrpcKadStubManager();
        }
        return instance;
    }

    public void ping(@NonNull NodeContact target, @NonNull Node currentNode, @NonNull GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));
        Ping pingOp = Ping.newBuilder().setNodeId(ByteString.copyFrom(currentNode.getId())).build();
        stub.ping(pingOp, new StreamObserver<Ping>() {
            @Override
            public void onNext(Ping value) {
                //handle ping success with lastSeen
                currentNode.setNodeAsSeen(target);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Encountered error in stream: " + t);
                t.printStackTrace();
                //handle onError
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished ping for [" + ByteString.copyFrom(target.getId()) + "]\n");
            }
        });
    }

    public void store(@NonNull NodeContact target, @NonNull Node currentNode, byte[] dataOwner, byte[] keyToStore, byte[] dataToStore,
                      @NonNull GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));
        Store storeOp = Store.newBuilder()
                .setReqNodeId(ByteString.copyFrom(currentNode.getId()))
                .setReqNodePort(currentNode.getPort())
                .setNodeId(ByteString.copyFrom(dataOwner))
                .setKey(ByteString.copyFrom(keyToStore)).setValue(ByteString.copyFrom(dataToStore))
                .build();
        stub.store(storeOp, new StreamObserver<Store>() {
            @Override
            public void onNext(Store value) {
                //handle next
                System.out.println("Store request sent to [" + Hex.toHexString(target.getId()) + "]");
                currentNode.storeInNode(dataOwner, keyToStore, dataToStore);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Encountered error in stream: " + t);
                t.printStackTrace();
                //handle error
            }

            @Override
            public void onCompleted() {
                //handle end/completion
            }
        });
    }

    public void findNode(@NonNull NodeContact target, @NonNull Node currentNode, @NonNull GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));
        FindNodeRequest request = FindNodeRequest.newBuilder()
                .setReqNodeId(ByteString.copyFrom(currentNode.getId()))
                .setReqNodePort(currentNode.getPort()) // TODO: ou target.getPort() ?
                .setNodeId(ByteString.copyFrom(target.getId()))
                .build();

        stub.findNode(request, new StreamObserver<FindNodeResponse>() {
            @Override
            public void onNext(FindNodeResponse value) {
                // findNodeResponse ?
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Encountered error in stream: " + t);
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished Find Node request for [" + ByteString.copyFrom(target.getId()) + "]\n");
            }
        });
    }

    public void findValue(@NonNull NodeContact target, @NonNull Node currentNode, @NonNull GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));
        FindValueRequest request = null; // todo: change this
        stub.findValue(request, new StreamObserver<FindValueResponse>() {
            @Override
            public void onNext(FindValueResponse value) {

            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Encountered error in stream: " + t);
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished Find Value request for [" + ByteString.copyFrom(target.getId()) + "]\n");
            }
        });
    }

}
