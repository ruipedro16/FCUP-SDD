package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Represents an auction of an item.
 * Item object contains all info related to the item to sell and its seller.
 */
@Data
public class Auction {
    private byte[] auctionID;
    private long maxTimeoutDuration;
    private long initTime;
    private Item auctionedItem;

    public Auction(@NonNull Item auctionedItem, long timeout) {
        this.auctionedItem = auctionedItem;
        this.maxTimeoutDuration = timeout;
        this.initTime = System.currentTimeMillis();
        this.auctionID = generateAuctionID();
    }

    /**
     * Generates an id for an auction from the init time and the item ID
     * @return Generated ID
     */
    private byte[] generateAuctionID() {
        String itemBytes = Arrays.toString(this.getAuctionedItem().getItemID());
        String timeBytes = String.valueOf(initTime);
        return itemBytes.concat(timeBytes).getBytes(); // TODO: restrain to a specific size maybe -> HASH
    }

    public String toString() {
        String s = """
                Auction:
                    Auction id: %s
                    Init time: %s
                    Max duration: %s
                    %s
                """;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date initDate = new Date(this.getInitTime());
        return String.format(s, Hex.toHexString(this.getAuctionID()), sdf.format(initDate), this.getMaxTimeoutDuration(), this.getAuctionedItem().toString());
    }
}
