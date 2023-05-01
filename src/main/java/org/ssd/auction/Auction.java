package org.ssd.auction;

import com.google.common.primitives.Longs;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.utils.CryptoUtils;

import java.io.Serializable;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents an auction of an item.
 * Item object contains all info related to the item to sell and its seller.
 */
@Data
public class Auction implements Serializable {
    private byte[] auctionID;
    private long maxTimeoutDuration;
    private long initTime; // time at which the auction started
    private final Item auctionedItem;
    private final PublicKey sellerPk;

    public Auction(@NonNull Item auctionedItem, long timeout, @NonNull PublicKey sellerPk) {
        this.auctionedItem = auctionedItem;
        this.maxTimeoutDuration = timeout;
        this.initTime = System.currentTimeMillis();
        this.sellerPk = sellerPk;
        this.auctionID = generateAuctionID();
    }

    /**
     * Generates an id for an auction from the init time and the item ID
     *
     * @return Generated ID
     */
    private byte[] generateAuctionID() {
        byte[] dataToHash = Arrays.concatenate(
                this.auctionedItem.getItemID(),
                Longs.toByteArray(this.initTime),
                Longs.toByteArray(this.maxTimeoutDuration),
                this.sellerPk.getEncoded()
        );

        return CryptoUtils.hash(dataToHash);
    }

    public String toString() {
        String s = """
                Auction:
                    Auction ID: %s
                    Seller PK: %s
                    Init time: %s
                    Max duration: %s
                    %s
                """;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date initDate = new Date(this.getInitTime());
        return String.format(s, Hex.toHexString(this.getAuctionID()), Hex.toHexString(this.sellerPk.getEncoded()),
                sdf.format(initDate), this.getMaxTimeoutDuration(), this.getAuctionedItem().toString());
    }
}
