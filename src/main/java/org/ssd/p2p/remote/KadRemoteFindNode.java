package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.routing.NodeContactDistanceComparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class KadRemoteFindNode implements KadAction {
    @Getter
    private final Node currentNode;

    @Getter
    private final byte[] targetID;
    private final Map<NodeContact, KadActionStatus> actionStatusMap;
    private final Map<NodeContact, Long> pendingResponsesMap;

    public KadRemoteFindNode(Node currentNode, byte[] targetID) {
        if (targetID == null) {
            throw new IllegalArgumentException("ID parameter cannot be null");
        }

        this.currentNode = currentNode;
        this.targetID = targetID;

        this.actionStatusMap = new TreeMap<>(new NodeContactDistanceComparator(targetID));
        this.pendingResponsesMap = new HashMap<>();

        this.actionStatusMap.put(currentNode.getCurrentNode(), KadActionStatus.RESPONDED);

        this.currentNode.getRoutingTable().getAllNodes()
                .forEach(nodeContact -> this.actionStatusMap.putIfAbsent(nodeContact, KadActionStatus.NOT_ASKED));
    }

    protected List<NodeContact> getKClosestResponded() {
        return this.actionStatusMap.keySet().stream()
                .filter(contact -> this.actionStatusMap.get(contact).equals(KadActionStatus.RESPONDED))
                .limit(KademliaConstants.K)
                .toList();
    }

    private boolean isDone() {
        if (this.pendingResponsesMap.size() >= KademliaConstants.ALPHA) {
            return false;
        }

        List<NodeContact> nextContacts = this.actionStatusMap.keySet().stream()
                .filter(contact -> this.actionStatusMap.get(contact).equals(KadActionStatus.NOT_ASKED))
                .limit(KademliaConstants.K)
                .toList();

        boolean shouldContinue = !nextContacts.isEmpty() || !this.pendingResponsesMap.isEmpty();

        if (shouldContinue) {
            for (NodeContact node : nextContacts) {
                this.actionStatusMap.put(node, KadActionStatus.AWAITING_RESPONSE);
                this.pendingResponsesMap.put(node, System.currentTimeMillis());
                this.currentNode.getClientManager().findNode(node, this);
                if (this.pendingResponsesMap.size() >= KademliaConstants.ALPHA) {
                    break;
                }
            }
            return false;
        }

        return true;
    }

    @Override
    public void trigger() {
        int totalTimeWaited = 0;
        int timeInterval = 20;

        while (!isDone()) {
            try {
                TimeUnit.MILLISECONDS.sleep(totalTimeWaited);
                totalTimeWaited += timeInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onSuccess(@NonNull NodeContact nodeContact, @NonNull List<NodeContact> nodeContacts) {
        this.pendingResponsesMap.remove(nodeContact);
        this.actionStatusMap.put(nodeContact, KadActionStatus.RESPONDED);
        this.currentNode.getRoutingTable().addContact(nodeContact);
        nodeContacts.forEach(node -> this.actionStatusMap.putIfAbsent(node, KadActionStatus.NOT_ASKED));
        isDone();
    }

    @Override
    public void onFailure(@NonNull NodeContact nodeContact) {
        this.pendingResponsesMap.remove(nodeContact);
        this.actionStatusMap.put(nodeContact, KadActionStatus.FAILED);
        this.currentNode.getRoutingTable().warnUnresponsiveContact(nodeContact);
        isDone();
    }
}
