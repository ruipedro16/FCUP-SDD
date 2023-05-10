package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;

import java.util.Arrays;
import java.util.List;

public class KadRemotePing implements KadAction {
    @Getter
    private final Node currentNode;
    private final byte[] targetID;

    public KadRemotePing(@NonNull Node currentNode, byte[] targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }

        this.currentNode = currentNode;
        this.targetID = targetID;
    }

    @Override
    public void trigger() {
        List<NodeContact> closestNodes = this.currentNode.getRoutingTable().getKClosestNodes(this.targetID);
        closestNodes.stream()
                .filter(contact -> Arrays.equals(contact.getId(), targetID))
                .findFirst()
                .ifPresent(contact -> this.currentNode.getClientManager().ping(contact, this));
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
