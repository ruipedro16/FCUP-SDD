package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class Auction {
    private byte[] auctionID;
    private Item auctionedItem;
    private Table priceTable;

    public Auction(@NonNull Item auctionedItem) {
        this.priceTable = new Table();
        this.auctionedItem = auctionedItem;
    }
}
