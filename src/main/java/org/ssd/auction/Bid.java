package org.ssd.auction;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.security.PublicKey;

@Data
public class Bid implements Serializable {
    private final byte[] itemID;
    private final double amount;
    private final PublicKey buyerPK;

    public Bid(byte[] itemID, double amount, @NonNull PublicKey buyerPK) {
        if (itemID == null) {
            throw new IllegalArgumentException();
        }

        this.itemID = itemID;
        this.amount = amount;
        this.buyerPK = buyerPK;
    }
}
