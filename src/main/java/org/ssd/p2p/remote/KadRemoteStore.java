package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoreData;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a Kademlia remote store action, which stores a {@link org.ssd.p2p.storage.StoreData}
 * in the distributed hash table (DHT) of a {@link org.ssd.p2p.Node}. This action triggers a remote find node action
 * to retrieve the k-closest nodes to the given key and sends the store request to each node except the current node.
 */
public record KadRemoteStore(@Getter Node currentNode, @Getter StoreData data) implements KadAction {
    public KadRemoteStore(@NonNull Node currentNode, @NonNull StoreData data) {
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
                .filter(contact -> !Arrays.equals(contact.getId(), this.currentNode.getCurrentNode().getId())) // do not forward the msg to self
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
