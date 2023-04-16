package org.ssd.p2p.remote;


import lombok.Getter;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.routing.Bucket;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class KadRemoteBroadcast implements KadAction {
    @Getter
    private final Node currentNode;

    @Getter
    private final int depth;

    @Getter
    private final byte[] messageID;

    @Getter
    private final byte[] message;

    public KadRemoteBroadcast(@NonNull Node currentNode, int depth, byte[] messageID, byte[] message) {
        if (messageID == null) {
            throw new IllegalArgumentException();
        }

        this.currentNode = currentNode;
        this.depth = depth;
        this.messageID = messageID;
        this.message = message;
    }

    @Override
    public void trigger() {
        SecureRandom random = new SecureRandom();
        this.currentNode.addToSeenMessages(messageID);

        for (int i = 0; i < KademliaConstants.B; i++) {
            Bucket bucket = this.currentNode.getRoutingTable().getBuckets().get(i);
            List<NodeContact> clonedBucket = new ArrayList<>(this.currentNode.getRoutingTable().getBuckets().get(i).getAllContacts());

            if (!bucket.isEmpty()) {
                for (int j = 0; j < KademliaConstants.MAX_BROADCAST_PER_DEPTH && !clonedBucket.isEmpty(); j++) {
                    int randomIndex = random.nextInt(clonedBucket.size());
                    NodeContact toBroadcast = clonedBucket.remove(randomIndex);
                    this.currentNode.getClientManager().broadCastMessage(toBroadcast, this);
                }
            }
        }
    }

    @Override
    public void onSuccess(@NonNull NodeContact nodeContact) {
        this.currentNode.getRoutingTable().addContact(nodeContact);
    }

    @Override
    public void onFailure(@NonNull NodeContact nodeContact) {
        this.currentNode.getRoutingTable().warnUnresponsiveContact(nodeContact);
    }
}
