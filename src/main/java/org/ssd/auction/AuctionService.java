package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import org.ssd.DHT;
import org.ssd.p2p.communication.*;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class AuctionService {
    private final Map<byte[], ActiveAuction> auctionMap;

    public AuctionService() {
        this.auctionMap = new HashMap<>();
    }

    public void addAuction(@NonNull ActiveAuction auction) {
        byte[] id = auction.getAuction().getAuctionedItem().getItemID();
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
        ActiveAuction runningAuction = new ActiveAuction(auction);
        byte[] id = auction.getAuctionedItem().getItemID(); // Use item id to publish, auctionId becomes useless
        this.auctionMap.put(id, runningAuction);
        AuctionMessage message = new AuctionMessage(runningAuction);
        DHT.getCommunicationManager().broadcastMessage(message);
    }

    public List<Bid> getBidsForAuction(byte[] auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException();
        }

        ActiveAuction auction = this.auctionMap.get(auctionId);
        return auction == null ? null : auction.getBids();
    }

    public void getNetworkAuctions() {
        GetActiveAuctionsMessage msg = new GetActiveAuctionsMessage();
        DHT.getCommunicationManager().broadcastMessage(msg);
    }

    public Bid getLastBid(byte[] auctionId) {
        if (auctionId == null) {
            throw new IllegalArgumentException();
        }

        ActiveAuction auction = this.auctionMap.get(auctionId);
        return auction == null ? null : auction.getMostRecentBid();
    }

    public ActiveAuction getAuctionById(byte[] id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.auctionMap.get(id); // may be null
    }

    public void publishEndedAuction(@NonNull ActiveAuction runningAuction) {
        List<Bid> bids = runningAuction.getBids();

        Bid highestBid = bids.stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElseThrow(NoSuchElementException::new);

        PayRequestMessage message = new PayRequestMessage(highestBid);
        DHT.getCommunicationManager().broadcastMessage(message);
    }

    public boolean endAuction(@NonNull String item) {
        Pair<byte[], ActiveAuction> runningAuction = getRunningAuctionsByItemWithList(item, getMyRunningAuctions());

        if (runningAuction == null) {
            return false;
        }

        publishEndedAuction(runningAuction.getSecond());
        return true;
    }

    public Pair<byte[], ActiveAuction> getRunningAuctionsByItemWithList(@NonNull String name,
                                                                             @NonNull Map<byte[], ActiveAuction> map) {
        return map.entrySet().stream()
                .filter(auctionEntry -> auctionEntry.getValue().getAuction().getAuctionedItem().getItemName().equals(name))
                .findFirst()
                .map(Pair::of)
                .orElse(null);
    }

    public Map<byte[], ActiveAuction> getMyRunningAuctions() {
        return this.auctionMap.entrySet().stream()
                .filter(auctionEntry -> auctionEntry.getValue().getAuction().getSellerPk().equals(DHT.getWallet().getPublicKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void sendAuction(@NonNull NodeContact nodeContact) {
        this.auctionMap.values().forEach(
                activeAuction -> {
                    MessageContent msg = new AuctionMessage(activeAuction);
                    DHT.getCommunicationManager().sendMessage(msg, nodeContact);
                }
        );
    }
}
