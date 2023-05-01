package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import org.ssd.DHT;
import org.ssd.p2p.communication.AuctionMessage;
import org.ssd.p2p.communication.BidMessage;
import org.ssd.p2p.communication.GetRunningAuctionMessage;
import org.ssd.p2p.communication.RequestPaymentMessage;

import java.util.*;

@Data
public class AuctionService {
    private final Map<byte[], RunningAuction> auctionMap;

    public AuctionService() {
        this.auctionMap = new HashMap<>();
    }

    public void addAuction(@NonNull RunningAuction auction) {
        byte[] id = auction.getAuction().getAuctionID(); // TODO: FIX THIS. ID should be ITEMID
        this.auctionMap.put(id, auction);
    }

    public boolean containsAuction(byte[] id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return auctionMap.containsKey(id);
    }

    public void addBid(@NonNull Bid bid) {
        byte[] auctionID = bid.getItemID();
        this.auctionMap.get(auctionID).placeBid(bid);
    }

    public void publishBid(@NonNull Bid bid) {
        byte[] auctionID = bid.getItemID();
        this.auctionMap.get(auctionID).placeBid(bid);
        BidMessage message = new BidMessage(bid);
        DHT.getCommunicationManager().broadcastMessage(message);
    }

    public void publishAuction(@NonNull Auction auction) {
        RunningAuction runningAuction = new RunningAuction(auction);
        byte[] id = auction.getAuctionID();
        this.auctionMap.put(id, runningAuction);
        AuctionMessage message = new AuctionMessage(runningAuction);
        DHT.getCommunicationManager().broadcastMessage(message);
    }

    public List<Bid> getBidsForAuction(byte[] auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException();
        }

        RunningAuction auction = this.auctionMap.get(auctionId);
        return auction == null ? null : auction.getBids();
    }

    public void getNetworkAuctions() {
        GetRunningAuctionMessage msg = new GetRunningAuctionMessage();
        DHT.getCommunicationManager().broadcastMessage(msg);
    }

    public Bid getLastBid(byte[] auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException();
        }

        RunningAuction auction = this.auctionMap.get(auctionId);
        return auction == null ? null : auction.getMostRecentBid();
    }

    public RunningAuction getAuctionById(byte[] id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.auctionMap.get(id); // may be null
    }

    public void publishEndedAuction(@NonNull RunningAuction runningAuction) {
        List<Bid> bids = runningAuction.getBids();

        Bid highestBid = bids.stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElseThrow(NoSuchElementException::new);

        RequestPaymentMessage message = new RequestPaymentMessage(highestBid);
        DHT.getCommunicationManager().broadcastMessage(message);
    }
}
