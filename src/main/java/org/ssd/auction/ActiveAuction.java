package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Data
public class ActiveAuction implements Serializable {
    private final Auction auction;
    private final List<Bid> bids;

    public ActiveAuction(@NonNull Auction auction) {
        this.auction = auction;
        this.bids = new ArrayList<>();
    }

    public Bid getMostRecentBid() {
        if (bids.isEmpty()) {
            return null;
        }

        return this.bids.get(this.bids.size() - 1);
    }

    public boolean containsBid(@NonNull Bid bid) {
        return this.bids.contains(bid);
    }

    public Bid getHighestBid() {
        return this.bids.stream()
                .max(Comparator.comparing(Bid::getAmount))
                .orElseThrow(NoSuchElementException::new);
    }

    public void placeBid(@NonNull Bid bid) {
        this.bids.add(bid);
    }

    public void printMostRecentBid() {
        Bid bid = getMostRecentBid();
        if (bid == null) {
            System.out.println("No bids");
        } else {
            System.out.println("Most recent bid: " + bid.toString());
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Auction ID: ").append(Hex.toHexString(this.auction.getAuctionID()));
        if (!this.bids.isEmpty()) {
            sb.append("\nBids: ");
            this.bids.forEach(bid -> sb.append(bid).append(','));
            sb.append('\n');
        } else {
            sb.append("\nBids: No bids so far\n");
        }
        return sb.toString();
    }
}
