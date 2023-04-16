package org.ssd.utils;

import com.google.protobuf.ByteString;
import lombok.NonNull;
import org.ssd.ProtoBroadcastMessage;
import org.ssd.ProtoContent;
import org.ssd.ProtoNodeContact;
import org.ssd.ProtoNodeContactList;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoredData;

import java.util.List;
import java.util.stream.Collectors;

public class gRPCUtils {
    public static ProtoNodeContact toGRPC(@NonNull NodeContact node) {
        return ProtoNodeContact.newBuilder()
                .setNodeIpAddress(node.getAddress().getHostAddress())
                .setNodeId(ByteString.copyFrom(node.getId()))
                .setNodePort(node.getPort())
                .build();
    }

    public static NodeContact fromGRPC(@NonNull ProtoNodeContact node) {
        return new NodeContact(
                Utils.getAddressFromString(node.getNodeIpAddress()),
                node.getNodePort(),
                node.getNodeId().toByteArray(),
                System.currentTimeMillis()
        );
    }

    public static List<NodeContact> fromGRPC(@NonNull List<ProtoNodeContact> nodes) {
        return nodes.stream().map(gRPCUtils::fromGRPC).collect(Collectors.toList());
    }

    public static ProtoNodeContactList toGRPC(@NonNull List<NodeContact> nodes) {
        ProtoNodeContactList.Builder builder = ProtoNodeContactList.newBuilder();
        nodes.stream()
                .map(gRPCUtils::toGRPC)
                .forEach(builder::addNodes);
        return builder.build();
    }

    public static ProtoContent toGRPC(@NonNull StoredData data)  {
        return ProtoContent.newBuilder()
                .setKey(ByteString.copyFrom(data.getKey()))
                .setValue(ByteString.copyFrom(data.getValue()))
                .setOriginalPublisherId(ByteString.copyFrom(data.getOriginalPublisherID()))
                .build();
    }

    public static Pair<byte[], byte[]> fromGRPC(@NonNull ProtoBroadcastMessage message) {
        return Pair.of(message.getMessageId().toByteArray(), message.getMessage().toByteArray());
    }

    public static StoredData fromGRPC(@NonNull ProtoContent content) {
        byte[] key = content.getKey().toByteArray();
        byte[] value = content.getValue().toByteArray();
        byte[] originalPublisherID = content.getOriginalPublisherId().toByteArray();

        return new StoredData(key, value, originalPublisherID);
    }
}
