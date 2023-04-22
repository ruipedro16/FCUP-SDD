package org.ssd.auction;

import com.google.protobuf.ByteString;
import lombok.Data;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies the item to be auctioned
 * Contains seller id, minimal amount to be auctioned for and item id
 */
@Data
public class Item implements Serializable {
    private final byte[] itemID;
    private final double minimalAmount;
    private final byte[] sellerId;
    private final PublicKey sellerPubKey;

    public Item(byte[] itemID, byte[] sellerId, PublicKey sellerPubKey, double minimalAmount) {
        if (itemID == null) {
            throw new IllegalArgumentException();
        }

        this.itemID = itemID;
        this.minimalAmount = minimalAmount;
        this.sellerId = sellerId;
        this.sellerPubKey = sellerPubKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.getMinimalAmount(), getMinimalAmount()) == 0 && Arrays.equals(getItemID(), item.getItemID()) && Arrays.equals(getSellerId(), item.getSellerId()) && Objects.equals(getSellerPubKey(), item.getSellerPubKey());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getMinimalAmount(), getSellerPubKey());
        result = 31 * result + Arrays.hashCode(getItemID());
        result = 31 * result + Arrays.hashCode(getSellerId());
        return result;
    }

    @Override
    public String toString() {
        String s = """
                Item:
                    Id: %s
                    Seller id: %s
                    Min Amount: %f
                """;
        return String.format(s, Hex.toHexString(this.getItemID()), Hex.toHexString(this.getSellerId()), this.getMinimalAmount());
    }
}
