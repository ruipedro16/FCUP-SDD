package org.ssd.p2p.communication;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.auction.AuctionService;
import org.ssd.auction.Bid;
import org.ssd.auction.RunningAuction;
import org.ssd.ledger.block.Block;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteBroadcast;
import org.ssd.p2p.remote.KadRemoteSendMessage;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.CryptoUtils;

import java.nio.charset.StandardCharsets;

/**
 * Routes messages based on type
 */
@Data
public class CommunicationManager {
    private final Node currentNode;
    private final Blockchain blockchain;
    private final AuctionService auctionService;

    public void broadcastMessage(@NonNull MessageContent message) {
        byte[] messageData = message.toString().getBytes(StandardCharsets.UTF_8);
        byte[] messageID = CryptoUtils.hash(messageData);

        new KadRemoteBroadcast(this.currentNode, 0, messageID, messageData).trigger();
    }

    public void sendMessage(@NonNull MessageContent message, @NonNull NodeContact nodeContact) {
        byte[] messageData = message.toString().getBytes(StandardCharsets.UTF_8);
        new KadRemoteSendMessage(this.currentNode, nodeContact.getId(), messageData).trigger();
    }

    public void handleIncomingMessage(@NonNull MessageContent message) {
        if (message.messageClass() == MessageClass.TRANSACTION_MESSAGE) { // BROADCAST_TRANSACTION
            TransactionMessage msg = (TransactionMessage) message;
            Transaction transaction = msg.getTransaction();
            System.out.println("Received a transaction");
            System.out.println("Adding transaction to the transaction pool");
            blockchain.getTransactionPool().addTransaction(transaction);
        } else if (message.messageClass() == MessageClass.BLOCK_MESSAGE) { // BROADCAST_BLOCK
            BlockMessage msg = (BlockMessage) message;
            Block block = msg.getBlock();
            System.out.println("Received block " + Hex.toHexString(block.getHeader().getHash()));
            System.out.println("Adding block to the blockchain...");
            blockchain.addBlock(block);
        } else if (message.messageClass() == MessageClass.AUCTION_MESSAGE) { // BROADCAST_AUCTION
            AuctionMessage msg = (AuctionMessage) message;
            RunningAuction auction = msg.getAuction();
            System.out.println("Received an auction");
            auctionService.addAuction(auction);
        } else if (message.messageClass() == MessageClass.BID_MESSAGE) {
            BidMessage msg = (BidMessage) message;
            Bid bid = msg.getBid();
            System.out.println("Received bid");
            auctionService.addBid(bid);
        } else if (message.messageClass() == MessageClass.GET_AUCTION_MESSAGE) {
            // todo: handle this in the auction service
        } else if (message.messageClass() == MessageClass.REQ_PAYMENT_MESSAGE) {
            PayRequestMessage msg = (PayRequestMessage) message;
            Bid bid = msg.getBid();
            if (DHT.getWallet().getPublicKey().equals(bid.getBuyerPK())) { // handle the payment if we are the bidder
                DHT.getWallet().createTransaction(bid.getBuyerPK(), bid.getAmount());
            }
        }
    }
}
