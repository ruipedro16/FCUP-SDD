package org.ssd.auction;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.ssd.p2p.Node;

import java.util.*;

@Getter
@Setter
public class AuctionsService {

    private final Node node;
    private final ArrayList<Auction> thisNodeAuctions;

    /*
     * Key      Auction ID
     * Value:   Subscribers (wallet ID or node ID?) of the subscribers of that auction
     */
    private final Map<byte[], Set<byte[]>> subscribers;

    /*
     * Auctions that haven't been closed yet
     */
    private final Map<byte[], Auction> runningAuctions;

    public AuctionsService(Node node) {
        this.node = node;
        this.thisNodeAuctions = new ArrayList<>();
        this.subscribers = new HashMap<>();
        this.runningAuctions = new HashMap<>();
    }

    public void subscribeAuction(byte[] subscriberID, byte[] auctionID) {
        if (subscriberID == null || auctionID == null) {
            throw new IllegalArgumentException();
        }

        if (!subscribers.containsKey(auctionID)) {
            subscribers.put(auctionID, new HashSet<>());
        }

        subscribers.get(auctionID).add(subscriberID);
    }

    public void removeSubscriber(byte[] subscriberID, byte[] auctionID) {
        if (subscriberID == null || auctionID == null) {
            throw new IllegalArgumentException();
        }

        Set<byte[]> subscribers = this.subscribers.get(auctionID);
        subscribers.remove(subscriberID);
    }

    public Auction getAuction(byte[] auctionID) {
        if (auctionID == null) {
            throw new IllegalArgumentException();
        }

        return this.runningAuctions.get(auctionID);
    }

    public void closeAuction(byte [] auctionID) {
        if (auctionID == null) {
            throw new IllegalArgumentException();
        }

        this.runningAuctions.remove(auctionID);
    }

    public void startAuction(@NonNull Item auctionedItem) {
        Auction newAuction = new Auction(auctionedItem, 0);//TODO: specify timeout or make it a default
        this.runningAuctions.put(newAuction.getAuctionID(), newAuction);
    }
}
