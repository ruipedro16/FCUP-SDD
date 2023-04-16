package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;

import java.util.Arrays;
import java.util.List;

public class KadRemoteSendMessage implements KadAction {
    @Getter
    private final Node currentNode;

    @Getter
    private final byte[] targetID;

    @Getter
    private final byte[] message;

    public KadRemoteSendMessage(@NonNull Node currentNode, byte[] targetID, byte[] message) {
        if (targetID == null) {
            throw new IllegalArgumentException();
        }

        this.currentNode = currentNode;
        this.targetID = targetID;
        this.message = message;
    }

    @Override
    public void trigger() {
        this.currentNode.getRoutingTable().getKClosestNodes(targetID)
                .stream()
                .filter(contact -> Arrays.equals(targetID, contact.getId()))
                .findFirst()
                .ifPresentOrElse(
                        // if present
                        nodeContact -> this.currentNode.getClientManager().sendMessage(nodeContact, this),

                        // or else
                        () -> {
                            KadRemoteFindNode lookUpAction = new KadRemoteFindNode(this.currentNode, targetID);
                            lookUpAction.trigger();
                            List<NodeContact> closest = lookUpAction.getKClosestResponded();
                            closest.stream()
                                    .filter(contact -> Arrays.equals(contact.getId(), targetID))
                                    .forEach(contact -> this.currentNode.getClientManager().sendMessage(contact, this));
                        });
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
