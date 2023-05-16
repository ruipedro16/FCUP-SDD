package org.ssd.p2p.communication;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.auction.ActiveAuction;
import org.ssd.auction.AuctionService;
import org.ssd.auction.Bid;
import org.ssd.ledger.block.Block;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteBroadcast;
import org.ssd.p2p.remote.KadRemoteSendMessage;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.io.IOException;

/**
 * Routes messages based on type
 */
@Data
public class CommunicationManager {
    private final Node currentNode;
    private final Blockchain blockchain;
    private final AuctionService auctionService;

    public void broadcastMessage(@NonNull Message message) {
        byte[] messageData;
        try {
            messageData = Utils.serializeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] messageID = CryptoUtils.hash(messageData);

        new KadRemoteBroadcast(this.currentNode, 0, messageID, messageData).trigger();
    }

    public void sendMessage(@NonNull Message message, @NonNull NodeContact nodeContact) {
        byte[] messageData;
        try {
            messageData = Utils.serializeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new KadRemoteSendMessage(this.currentNode, nodeContact.getId(), messageData).trigger();
    }

    public void messageReceiver(@NonNull NodeContact sender, byte[] msg) throws IOException, ClassNotFoundException {
        if (msg == null) {
            throw new IllegalArgumentException();
        }

        Message msgContent = (Message) Utils.deserializeBytes(msg);
        handleIncomingMessage(sender, msgContent);
    }

    public void handleIncomingMessage(@NonNull NodeContact sender, @NonNull Message message) {
        if (message.messageClass() == MessageClass.TRANSACTION_MESSAGE) {
            TransactionMessage msg = (TransactionMessage) message;
            Transaction transaction = msg.getTransaction();
            System.out.println("Received a transaction");
            System.out.println("Adding transaction to the transaction pool");
            blockchain.getTransactionPool().addTransaction(transaction);
        } else if (message.messageClass() == MessageClass.BLOCK_MESSAGE) {//ToDo: verify if only the relevant block is added, it seems after a payment is made, all nodes mine and add their own block to all DHT
            BlockMessage msg = (BlockMessage) message;
            Block block = msg.getBlock();
            System.out.println("Received block " + Hex.toHexString(block.getHeader().getHash()));
            System.out.println("Adding block to the blockchain...");
            blockchain.addBlock(block);
        } else if (message.messageClass() == MessageClass.AUCTION_MESSAGE) {
            AuctionMessage msg = (AuctionMessage) message;
            ActiveAuction auction = msg.getAuction();
            System.out.println("Received an auction");
            auctionService.addAuction(auction);
        } else if (message.messageClass() == MessageClass.BID_MESSAGE) {
            BidMessage msg = (BidMessage) message;
            Bid bid = msg.getBid();
            System.out.println("Received bid");
            auctionService.addBid(bid);
        } else if (message.messageClass() == MessageClass.GET_AUCTION_MESSAGE) {
            System.out.println("Getting auctions in the network");
            auctionService.sendAuction(sender);
        } else if (message.messageClass() == MessageClass.REQ_PAYMENT_MESSAGE) {
            PayRequestMessage msg = (PayRequestMessage) message;
            Bid bid = msg.getBid();
            if (DHT.getWallet().getPublicKey().equals(bid.getBuyerPK())) { // handle the payment if we are the bidder
                System.out.println("Requesting payment");
                DHT.getWallet().createTransaction(bid.getBuyerPK(), bid.getAmount());
            }
            //remove auction from active auctions
            auctionService.getAuctionMap().remove(ByteString.copyFrom(bid.getItemID()));
        } else if (message.messageClass() == MessageClass.REQ_BLOCKCHAIN) {
            RequestBlockchainMessage msg = (RequestBlockchainMessage) message;
            this.blockchain.getBlocks().forEach(
                    block -> {
                        BlockMessage blockMessage = new BlockMessage(block);
                        sendMessage(blockMessage, sender);
                    }
            );
        }
    }
}
