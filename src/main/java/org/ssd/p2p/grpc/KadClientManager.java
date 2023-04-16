package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.*;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.remote.*;
import org.ssd.p2p.storage.StoredData;
import org.ssd.utils.gRPCUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KadClientManager {
    private final Map<byte[], ManagedChannel> channels;

    public KadClientManager() {
        this.channels = new ConcurrentHashMap<>();
    }

    /**
     * Initialize an unsecure channel to a specific address and port (used for testing)
     *
     * @param target Address
     * @return ManagedChannel Object to give to Stubs
     */
    private ManagedChannel initUnsecureChannel(@NonNull NodeContact target) {
        return ManagedChannelBuilder
                .forAddress(target.getAddress().getHostAddress(), target.getPort())
                .usePlaintext()
                .build();
    }

    public ManagedChannel getChannel(@NonNull NodeContact nodeContact) {
        byte[] nodeId = nodeContact.getId();

        if (channels.containsKey(nodeId)) {
            ManagedChannel channelValue = this.channels.get(nodeId);
            if (channelValue.isTerminated() || channelValue.isShutdown()) {
                return null; //found but not available
            }
            return channelValue; //found
        }
        return null; // not found
    }

    public void saveChannel(@NonNull NodeContact target, @NonNull ManagedChannel channel) {
        this.channels.put(target.getId(), channel);
    }

    public ManagedChannel initChannel(@NonNull NodeContact target) {
        // verify if channel exists
        ManagedChannel channel = getChannel(target);
        if (channel == null || channel.isTerminated() || channel.isShutdown()) {
            // init
            ManagedChannel created = initUnsecureChannel(target);
            // save connection to router's list
            saveChannel(target, created);
            return created;
        } else if (!channel.isShutdown() && !channel.isTerminated()) {
            return channel;
        }
        return null;
    }


    public void shutdownChannel(@NonNull NodeContact target) {
        ManagedChannel channel = getChannel(target);
        if (channel.isShutdown() || channel.isTerminated()) {
            return;
        }
        channel.shutdown();
        this.channels.remove(target.getId());
    }

    private P2PGrpcServiceGrpc.P2PGrpcServiceStub initStub(@NonNull NodeContact nodeContact) {
        ManagedChannel channel = getChannel(nodeContact);
        return P2PGrpcServiceGrpc.newStub(channel);
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
        Node currentNode = storeAction.getCurrentNode();
        StoredData data = storeAction.getData();

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
                StoredData data = gRPCUtils.fromGRPC(response.getFoundValue());
                // contentLookupAction.onSuccess(recipient, data);
            } else {

            }
        } catch (StatusRuntimeException e) {
            shutdownChannel(recipient);
            contentLookupAction.onFailure(recipient);
        }
    }

    public void sendMessage(@NonNull NodeContact recipient, @NonNull KadRemoteSendMessage sendMessageAction) {
        Node currentNode = sendMessageAction.getCurrentNode();
        byte[] message = sendMessageAction.getMessage();

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoMessage protoMessage = ProtoMessage.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC( currentNode.getCurrentNode()))
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

    public void broadCastMessage(@NonNull NodeContact recipient, @NonNull KadRemoteBroadcast broadcastAction) {
        Node currentNode = broadcastAction.getCurrentNode();
        System.out.println("Broadcasting message from node " + Hex.toHexString(currentNode.getCurrentNode().getId()));

        P2PGrpcServiceGrpc.P2PGrpcServiceBlockingStub blockingStub = initBlockingStub(recipient);

        ProtoBroadcastMessage message = ProtoBroadcastMessage.newBuilder()
                .setSendingNode(gRPCUtils.toGRPC(currentNode.getCurrentNode()))
                .setDepth(broadcastAction.getDepth())
                .setMessage(ByteString.copyFrom(broadcastAction.getMessage()))
                .setMessageId(ByteString.copyFrom(broadcastAction.getMessageID()))
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
