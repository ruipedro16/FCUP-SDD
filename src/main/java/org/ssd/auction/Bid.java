package org.ssd.auction;

import lombok.Data;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.security.PublicKey;

@Data
public class Bid implements Serializable {
    private final byte[] itemID;
    private final double amount;
    private final PublicKey buyerPK;

    public Bid(byte[] itemID, double amount, PublicKey buyerPK) {
        if (itemID == null) {
            throw new IllegalArgumentException();
        }

        this.itemID = itemID;
        this.amount = amount;
        this.buyerPK = buyerPK;
    }

    @Override
    public String toString() {
        String s = """
                Bid:
                    Item ID: %s
                    Amount: %s
                """;
        return String.format(s, Hex.toHexString(this.getItemID()), this.getAmount());
    }
}
