package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

/**
 * Specifies the item to be auctioned
 * Contains seller id, minimal amount to be auctioned for and item id
 */
@Data
public class Item implements Serializable {
    private final String itemName;
    private final byte[] itemID;
    private final double minimumAmount;
    private final PublicKey sellerPubKey;

    public Item(@NonNull String itemName, @NonNull PublicKey sellerPubKey, double minimumAmount) {
        this.itemName = itemName;
        this.minimumAmount = minimumAmount;
        this.sellerPubKey = sellerPubKey;

        byte[] dataToHash = Arrays.concatenate(
                this.itemName.getBytes(StandardCharsets.UTF_8),
                this.sellerPubKey.getEncoded(),
                Utils.toByteArray(this.minimumAmount)
        );

        this.itemID = CryptoUtils.hash(dataToHash);
    }

    @Override
    public String toString() {
        String s = """
                Item:
                    Item Name: %s
                    Id: %s
                    Seller id: %s
                    Min Amount: %f
                """;
        return String.format(s, this.getItemName(), Hex.toHexString(this.getItemID()), Hex.toHexString(this.getSellerPubKey().getEncoded()), this.getMinimumAmount());
    }
}
