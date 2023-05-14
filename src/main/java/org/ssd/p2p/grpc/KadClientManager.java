package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.*;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.*;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoreData;
import org.ssd.utils.gRPCUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KadClientManager {
    private final Map<byte[], ManagedChannel> channels;

    public KadClientManager() {
        this.channels = new ConcurrentHashMap<>();
    }

    public ManagedChannel getChannel(@NonNull NodeContact nodeContact) {
        byte[] nodeId = nodeContact.getId();
        ManagedChannel channel = null;

        if (this.channels.containsKey(nodeId)) {
            channel = this.channels.get(nodeId);
            if (channel.isTerminated() || channel.isShutdown()) {
                channel = null;
            }
        }

        if (channel == null) {
            String address = nodeContact.getAddress().getHostAddress();
            int port = nodeContact.getPort();

            channel = ManagedChannelBuilder.forAddress(address, port)
                    .usePlaintext()
                    .build();

            this.channels.put(nodeId, channel);
        }

        return channel;
    }

    public void shutdownChannel(@NonNull NodeContact target) {
        ManagedChannel channel = getChannel(target);
        if (channel.isShutdown() || channel.isTerminated()) {
            return;
        }
        channel.shutdown();
        this.channels.remove(target.getId());
    }

    private P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub initBlockingStub(@NonNull NodeContact nodeContact) {
        ManagedChannel channel = getChannel(nodeContact);
        return P2PGrpcServiceGrpc.newBlockingStub(channel);
    }

    public void ping(@NonNull NodeContact recipient, @NonNull KadRemotePing pingAction) {
        Node currentNode = pingAction.getCurrentNode();
        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);
        ProtoNodeContact reqNode = gRPCUtils.toGRPC(currentNode.getCurrentNode());

        try {
            ProtoNodeContact response = blockingStub.ping(reqNode);
            pingAction.onSuccess(recipient);
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            pingAction.onFailure(recipient);
        }
    }

    public void store(@NonNull NodeContact recipient, @NonNull KadRemoteStore storeAction) {
        Node currentNode = storeAction.currentNode();
        StoreData data = storeAction.data();

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoContent content = ProtoContent.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setOriginalPublisherId(ByteString.copyFrom(data.getOriginalPublisherID()))
                .setKey(ByteString.copyFrom(data.getKey()))
                .setValue(ByteString.copyFrom(data.getValue()))
                .build();

        try {
            ProtoContent response = blockingStub.store(content);
            storeAction.onSuccess(recipient);
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            storeAction.onFailure(recipient);
        }
    }

    public void findNode(@NonNull NodeContact recipient, @NonNull KadRemoteFindNode lookupAction) {
        Node currentNode = lookupAction.getCurrentNode();
        byte[] targetID = lookupAction.getTargetID();

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoTargetContact target = ProtoTargetContact.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setTarget(ByteString.copyFrom(targetID))
                .build();

        try {
            ProtoFindNodeResponse response = blockingStub.findNode(target);
            List<NodeContact> responseContacts = gRPCUtils.fromGRPC(response.getFoundNodes().getNodesList());
            lookupAction.onSuccess(recipient, responseContacts);
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            lookupAction.onFailure(recipient);
        }
    }

    public void findValue(@NonNull NodeContact recipient, @NonNull KadRemoteFindValue contentLookupAction) {
        Node currentNode = contentLookupAction.getCurrentNode();
        byte[] key = contentLookupAction.getTargetKey();

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoTargetContact targetContact = ProtoTargetContact.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setTarget(ByteString.copyFrom(key))
                .build();

        try {
            ProtoFindValueResponse response = blockingStub.findValue(targetContact);
            if (response.getDataType() == DataType.FOUND_VALUE) {
                StoreData data = gRPCUtils.fromGRPC(response.getFoundValue());
                contentLookupAction.onSuccess(recipient, data);
            } else {
                List<NodeContact> nodeContactList = gRPCUtils.fromGRPC(response.getFoundNodes().getNodesList());
                contentLookupAction.onSuccess(recipient, nodeContactList);
            }
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            contentLookupAction.onFailure(recipient);
        }
    }

    public void sendMessage(@NonNull NodeContact recipient, @NonNull KadRemoteSendMessage sendMessageAction) {
        Node currentNode = sendMessageAction.currentNode();
        byte[] message = sendMessageAction.message();
        System.out.println("Sending message to node " + Hex.toHexString(recipient.getId()));

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoMessage protoMessage = ProtoMessage.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setMessage(ByteString.copyFrom(message))
                .build();

        try {
            ProtoMessageResponse response = blockingStub.sendMessage(protoMessage);
            sendMessageAction.onSuccess(recipient);
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            sendMessageAction.onFailure(recipient);
        }
    }

    public void broadCastMessage(@NonNull NodeContact recipient, @NonNull KadRemoteBroadcast broadcastAction, int depth) {
        Node currentNode = broadcastAction.currentNode();
        System.out.println("Broadcasting message to node " + Hex.toHexString(recipient.getId()));

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoBroadcastMessage message = ProtoBroadcastMessage.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setDepth(depth)
                .setMessage(ByteString.copyFrom(broadcastAction.message()))
                .setMessageId(ByteString.copyFrom(broadcastAction.messageID()))
                .build();

        try {
            ProtoNodeContact response = blockingStub.broadcastMessage(message);
            broadcastAction.onSuccess(recipient);
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            broadcastAction.onFailure(recipient);
        }
    }
}
