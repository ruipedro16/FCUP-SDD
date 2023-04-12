package org.ssd.p2p.remote;

import lombok.AllArgsConstructor;
import org.ssd.p2p.KadAction;
import org.ssd.p2p.Node;
import org.ssd.p2p.NodeContact;

import java.util.Arrays;
import java.util.List;

/**
 * Remote call to ping node
 */
@AllArgsConstructor
public class KadRemotePing implements KadAction {
    private final Node currentNode;
    private final byte[] targetNodeID;

    @Override
    public void trigger() {
        List<NodeContact> kClosestNodes = currentNode.getRoutingTable().getKClosestNodes(targetNodeID);

        for (NodeContact targetContact : kClosestNodes) {
            if (Arrays.equals(targetContact.getId(), targetNodeID)) {
                currentNode.getStubRouter().ping(targetContact, currentNode);
                return;
            }
        }
    }
}
