package org.ssd.p2p.communication;

import lombok.Data;
import lombok.NonNull;
import org.ssd.auction.AuctionService;
import org.ssd.ledger.block.Blockchain;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteBroadcast;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.CryptoUtils;

import java.nio.charset.StandardCharsets;

@Data
public class CommunicationManager {
    private final Node node;
    private final Blockchain blockchain;
    private final AuctionService auctionService;

    public void broadcastMessage(@NonNull Message message) {
        byte[] messageData = message.toString().getBytes(StandardCharsets.UTF_8);
        byte[] messageID = CryptoUtils.hash(messageData);

        new KadRemoteBroadcast(this.node,0, messageID, messageData)
    }

    public void handleMessage(@NonNull NodeContact, byte[] messageData) {
        // TODO:
    }
}
