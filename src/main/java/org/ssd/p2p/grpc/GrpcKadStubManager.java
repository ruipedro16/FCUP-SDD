package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.ssd.P2PGrpcServiceGrpc;
import org.ssd.Ping;
import org.ssd.Store;
import org.ssd.p2p.Node;
import org.ssd.utils.Triple;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Kademlia messages (only) stub builder and handler to send messages
 */
public class GrpcKadStubManager {

    private static GrpcKadStubManager instance;

    public GrpcKadStubManager() {
        instance = this;
    }

    public static GrpcKadStubManager getInstance() {
        if (instance == null) {
            instance = new GrpcKadStubManager();
        }
        return instance;
    }

    public void ping(Triple<byte[], InetAddress, Integer> target, Node currentNode, GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));
        Ping pingOp = Ping.newBuilder().setNodeId(ByteString.copyFrom(currentNode.getId())).build();
        stub.ping(pingOp, new StreamObserver<Ping>() {
            @Override
            public void onNext(Ping value) {
                //handle ping success with lastSeen
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                //handle onError
            }
            @Override
            public void onCompleted() {
                System.out.println("Finished ping for [" + ByteString.copyFrom(target.getFirst()) + "]\n");
            }
        });
    }

    public void store(Triple<byte[], InetAddress, Integer> target, Node currentNode, byte[] dataOwner, byte[] keyToStore, byte[] dataToStore, GrpcStubRouter originRouter) {
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
            }
            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                //handle error
            }
            @Override
            public void onCompleted() {
                //handle end/completion
            }
        });
    }

    public void findNode(Triple<byte[], InetAddress, Integer> target, Node currentNode, GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));

    }

    public void findValue(Triple<byte[], InetAddress, Integer> target, Node currentNode, GrpcStubRouter originRouter) {
        P2PGrpcServiceGrpc.P2PGrpcServiceStub stub = P2PGrpcServiceGrpc.newStub(originRouter.initChannel(target));

    }

}
