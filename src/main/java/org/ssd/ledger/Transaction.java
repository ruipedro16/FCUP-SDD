package org.ssd.ledger;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.PublicKey;

@Data

public class Transaction {
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double amount;
    private final long timestamp;
    private final byte[] signature;

    public Transaction(PublicKey sender, PublicKey recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.signature = null; // generateSignature(); todo:
    }

    protected byte[] getBytes() {
        // todo
        return null;
    }
}
