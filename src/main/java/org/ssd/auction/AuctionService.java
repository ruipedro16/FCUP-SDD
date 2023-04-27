package org.ssd.auction;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class AuctionService {
    private static final Scanner sc = new Scanner(System.in);

    private final Map<byte[], RunningAuction> auctionMap;

    private Thread thread;

    public AuctionService() {
        this.auctionMap = new HashMap<>();
        this.thread = null;
    }

    public void addAuction(@NonNull RunningAuction auction) {
        byte[] id = auction.getAuction().getAuctionID(); // TODO: FIX THIS. ID should be ITEMID
        this.auctionMap.put(id, auction);
    }

    public void addBid(@NonNull Bid bid) {
        byte[] auctionID = bid.getItemID();
        this.auctionMap.get(auctionID).placeBid(bid);
    }
}
