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
    private final Item auctionedItem;
    private final PublicKey sellerPk;

    public Auction(@NonNull Item auctionedItem, @NonNull PublicKey sellerPk) {
        this.auctionedItem = auctionedItem;
        this.sellerPk = sellerPk;
    }

    public String toString() {
        String s = """
                Auction:
                    Auction ID: %s
                    Seller PK: %s
                    %s
                """;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        return String.format(s, Hex.toHexString(this.auctionedItem.getItemID()), Hex.toHexString(this.sellerPk.getEncoded()));
    }
}
