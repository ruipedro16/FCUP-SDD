package org.ssd.ledger;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.Bid;

import java.security.PublicKey;

@Data

public class Transaction {
    private Bid bid;
    private final long timestamp;
    private final byte[] signature;

    public Transaction(/* PublicKey sender, PublicKey recipient, double amount*/) {
        /*
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;

         */
        this.timestamp = System.currentTimeMillis();
        this.signature = null; // generateSignature(); todo:
    }

    protected byte[] getBytes() {
        // todo
        return null;
    }
}
