package org.ssd.p2p.remote;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.routing.NodeContactDistanceComparator;
import org.ssd.p2p.storage.StoreData;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KadRemoteFindValue implements KadAction {
    @Getter
    private final Node currentNode;

    @Getter
    private final byte[] targetKey;
    private final Map<NodeContact, KadActionStatus> actionStatusMap;
    private final Map<NodeContact, Long> pendingResponsesMap;
    private byte[] foundValue;

    public KadRemoteFindValue(@NonNull Node currentNode, byte[] targetKey) {
        if (targetKey == null) {
            throw new IllegalArgumentException();
        }

        this.currentNode = currentNode;
        this.targetKey = targetKey;

        this.actionStatusMap = new TreeMap<>(new NodeContactDistanceComparator(targetKey));

        this.pendingResponsesMap = new HashMap<>();
        this.actionStatusMap.put(this.currentNode.getCurrentNode(), KadActionStatus.RESPONDED);

        this.currentNode.getRoutingTable().getAllNodes()
                .forEach(nodeContact ->
                        this.actionStatusMap.putIfAbsent(nodeContact, KadActionStatus.NOT_ASKED));

        this.foundValue = null;
    }

    private boolean isDone() {
        if (this.foundValue != null) {
            return true;
        }

        if (pendingResponsesMap.size() >= KademliaConstants.ALPHA) {
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
                this.currentNode.getClientManager().findValue(node, this);
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
    public void onSuccess(@NonNull NodeContact nodeContact, @NonNull StoreData storeData) {
        /*
         * Handles a found value
         */
        if (this.foundValue != null) {
            this.currentNode.getRoutingTable().addContact(nodeContact);
        } else {
            this.foundValue = storeData.getValue();
            this.pendingResponsesMap.remove(nodeContact);
            this.actionStatusMap.put(nodeContact, KadActionStatus.RESPONDED);
            this.currentNode.getRoutingTable().addContact(nodeContact);
            this.currentNode.getDht().store(storeData);
        }
    }

    @Override
    public void onSuccess(@NonNull NodeContact nodeContact, @NonNull List<NodeContact> nodeContacts) {
        this.pendingResponsesMap.remove(nodeContact);
        this.actionStatusMap.put(nodeContact, KadActionStatus.RESPONDED);

        this.currentNode.getRoutingTable().addContact(nodeContact);
        nodeContacts.forEach(contact -> this.actionStatusMap.putIfAbsent(contact, KadActionStatus.NOT_ASKED));
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
