package org.ssd.ledger;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.security.PublicKey;

@Data
public class TransactionOutput {
    private final byte[] id;
    private final PublicKey receiver;
    private final double amount;
    private final byte[] parentID; // ID of the transaction output that is being used as the input for this transaction

    public TransactionOutput(PublicKey receiver, double amount, byte[] parentID) {
        this.receiver = receiver;
        this.amount = amount;
        this.parentID = parentID;
        this.id = computeID();
    }

    private byte[] computeID() {
        assert this.receiver != null;
        assert this.parentID != null;

        byte[] dataToHash = Arrays.concatenate(
          this.receiver.getEncoded(),
                Utils.toByteArray(amount),
                parentID
        );

        return CryptoUtils.hash(dataToHash);
    }

    /**
     * Checks if the specified public key is the owner of this coin.
     *
     * @param pk the public key to check against the receiver public key of this transaction output
     * @return true if the specified public key is the owner of the coin, false otherwise
     * @throws NullPointerException if the specified public key is null
     */
    public boolean checkOwner(@NonNull PublicKey pk) {
        return this.receiver.equals(pk);
    }
}
