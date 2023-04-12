package org.ssd.p2p.remote;

import lombok.AllArgsConstructor;
import org.ssd.p2p.Bucket;
import org.ssd.p2p.KadAction;
import org.ssd.p2p.Node;
import org.ssd.p2p.NodeContact;
import org.ssd.p2p.remote.message.StoreMessage;

/**
 * Action meant to be instanced when a message should be emitted to the dht.
 * (ex: Auction start or generic data needed to be stored on the dht network)
 */
public class KadRemoteStore implements KadAction {

    private final Node ourNode;
    private final StoreMessage msg;

    public KadRemoteStore(Node node, StoreMessage msgToStore) {
        //add content to a message for the store
        this.ourNode = node;
        this.msg = msgToStore;
    }

    /**
     *
     */
    @Override
    public void trigger() {
        //k-closest nodes //todo: should we use the known ones or create a list after attempting a lookup/ping?
        //add message locally
        this.ourNode.storeInNode(msg.getDataOwnerId(), msg.getKey(), msg.getValue());

        // Iterate through all nodes
        for (NodeContact contact : this.ourNode.findClosestNodes(msg.getKey())) {
            // propagate it to nearby nodes
            this.store(contact, msg.getDataOwnerId(), msg.getKey(), msg.getValue());
        }

    }

    /**
     * Private call wrapper to RPC store
     *
     * @param target      target to store data to
     * @param dataOwnerId data owner from where data originated from
     * @param key         key of the data
     * @param dataValue   value to store
     */
    private void store(NodeContact target, byte[] dataOwnerId, byte[] key, byte[] dataValue) {
        this.ourNode.getRoutingTable().getKadStubRouter().store(target, this.ourNode, dataOwnerId, key, dataValue, this.ourNode.getRoutingTable().getStubRouter());
    }

}
