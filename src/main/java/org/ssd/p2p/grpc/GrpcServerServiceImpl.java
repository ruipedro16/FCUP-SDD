package org.ssd.p2p.grpc;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.*;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteBroadcast;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoreData;
import org.ssd.utils.Pair;
import org.ssd.utils.Utils;
import org.ssd.utils.gRPCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GrpcServerServiceImpl extends P2PGrpcServiceGrpc.P2PGrpcServiceImplBase {
    private final Node currentNode;
    private final List<BiConsumer<NodeContact, byte[]>> messageConsumers;

    public GrpcServerServiceImpl(@NonNull Node currentNode) {
        this.currentNode = currentNode;
        this.messageConsumers = new ArrayList<>();
    }

    public void registerMessageSubscriber(@NonNull BiConsumer<NodeContact, byte[]> consumer) {
        this.messageConsumers.add(consumer);
    }

    /**
     * Asynchronously handles the specified Protobuf message. If the message is a ProtoNodeContact message,
     * creates a new NodeContact object from the message fields and adds it to the current node's routing table.
     *
     * @param msg the Protobuf message to handle
     * @throws NullPointerException if msg is null
     */
    private void handleProtoNodeContactMsg(@NonNull GeneratedMessageV3 msg) {
        new Thread(() -> {
            if (msg instanceof ProtoNodeContact nodeMsg) {
                NodeContact incomingContact = new NodeContact(Utils.getAddressFromString(nodeMsg.getNodeIpAddress()),
                        nodeMsg.getNodePort(), nodeMsg.getNodeId().toByteArray(), System.currentTimeMillis());

                this.currentNode.getRoutingTable().addContact(incomingContact);
            }
        }).start();
    }

    @Override
    public void ping(ProtoNodeContact request, StreamObserver<ProtoNodeContact> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] PING request from " + request.getNodeId());
        handleProtoNodeContactMsg(request);
        ProtoNodeContact response = gRPCUtils.toGRPC(this.currentNode.getCurrentNode());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void store(ProtoContent request, StreamObserver<ProtoContent> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] STORE request from " + Hex.toHexString(request.getSendingNode().getNodeId().toByteArray()));
        handleProtoNodeContactMsg(request.getSendingNode());

        byte[] key = request.getKey().toByteArray();
        byte[] value = request.getValue().toByteArray();

        this.currentNode.getDht().store(key, value, request.getOriginalPublisherId().toByteArray());

        ProtoContent response = ProtoContent.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(this.currentNode.getCurrentNode()))
                .setKey(request.getKey())
                .setValue(request.getValue())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findNode(ProtoTargetContact request, StreamObserver<ProtoFindNodeResponse> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] FIND_NODE request from " + Hex.toHexString(request.getSendingNode().getNodeId().toByteArray()));
        handleProtoNodeContactMsg(request.getSendingNode());

        byte[] targetID = request.getTarget().toByteArray();
        List<NodeContact> contactList = this.currentNode.getRoutingTable().getKClosestNodes(targetID);

        ProtoFindNodeResponse response = ProtoFindNodeResponse.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(this.currentNode.getCurrentNode()))
                .setFoundNodes(gRPCUtils.toGRPC(contactList))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findValue(ProtoTargetContact request, StreamObserver<ProtoFindValueResponse> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] FIND_VALUE request from " + Hex.toHexString(request.getSendingNode().getNodeId().toByteArray()));
        handleProtoNodeContactMsg(request.getSendingNode());

        byte[] targetID = request.getTarget().toByteArray();
        boolean found = this.currentNode.getDht().containsKey(targetID);

        if (found) { // if the value is found in the DHT, we return it
            StoreData data = this.currentNode.getDht().get(targetID);

            ProtoFindValueResponse response = ProtoFindValueResponse.newBuilder()
                    .setSendingNode(gRPCUtils.toGRPC(this.currentNode.getCurrentNode()))
                    .setDataType(DataType.FOUND_VALUE)
                    .setFoundValue(gRPCUtils.toGRPC(data))
                    .build();

            responseObserver.onNext(response);
        } else {
            List<NodeContact> kClosest = this.currentNode.getRoutingTable().getKClosestNodes(targetID);

            ProtoFindValueResponse response = ProtoFindValueResponse.newBuilder()
                    .setSendingNode(gRPCUtils.toGRPC(this.currentNode.getCurrentNode()))
                    .setDataType(DataType.FOUND_NODES)
                    .setFoundNodes(gRPCUtils.toGRPC(kClosest))
                    .build();

            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void sendMessage(ProtoMessage request, StreamObserver<ProtoMessageResponse> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] SEND_MESSAGE request from " + Hex.toHexString(request.getSendingNode().getNodeId().toByteArray()));
        handleProtoNodeContactMsg(request.getSendingNode());

        NodeContact nodeContact = gRPCUtils.fromGRPC(request.getSendingNode());
        byte[] msgBytes = request.getMessage().toByteArray();

        ProtoMessageResponse response = ProtoMessageResponse.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(this.currentNode.getCurrentNode()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        this.messageConsumers.forEach(consumer -> consumer.accept(nodeContact, msgBytes));
    }

    @Override
    public void broadcastMessage(ProtoBroadcastMessage request, StreamObserver<ProtoNodeContact> responseObserver) {
        System.out.println("[" + Hex.toHexString(this.currentNode.getCurrentNode().getId()) + "] BROADCAST_MESSAGE request from " + Hex.toHexString(request.getSendingNode().getNodeId().toByteArray()));
        handleProtoNodeContactMsg(request.getSendingNode());

        ProtoNodeContact response = gRPCUtils.toGRPC(this.currentNode.getCurrentNode());

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        Pair<byte[], byte[]> msgIdPair = gRPCUtils.fromGRPC(request);
        byte[] msgID = msgIdPair.getFirst();
        byte[] msg = msgIdPair.getSecond();

        if (this.currentNode.addToSeenMessages(msgID)) {
            new KadRemoteBroadcast(this.currentNode, request.getDepth(), msgID, msg); // Todo: This operation is never triggered
            NodeContact contact = gRPCUtils.fromGRPC(request.getSendingNode());
            Context.current()
                    .fork()
                    .run(() -> this.messageConsumers.forEach(consumer -> consumer.accept(contact, msg)));
        }
    }
}
