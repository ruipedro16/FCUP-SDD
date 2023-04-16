package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoredData;

import java.util.Arrays;
import java.util.List;

public record KadRemoteStore(@Getter Node currentNode, @Getter StoredData data) implements KadAction {
    public KadRemoteStore(@NonNull Node currentNode, @NonNull StoredData data) {
        this.currentNode = currentNode;
        this.data = data;
    }

    @Override
    public void trigger() {
        this.currentNode.getDht().store(this.data);

        KadRemoteFindNode lookUpAction = new KadRemoteFindNode(this.currentNode, this.data.getKey());
        lookUpAction.trigger();
        List<NodeContact> nextContacts = lookUpAction.getKClosestResponded();
        nextContacts.stream()
                .filter(contact -> !Arrays.equals(contact.getId(), this.currentNode.getCurrentNode().getId()))
                .forEach(contact -> this.currentNode.getClientManager().store(contact, this));
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
