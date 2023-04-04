package org.ssd.ledger.transactions;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.security.PublicKey;

@Data
public class TransactionOutput {
    private byte[] ID;
    private PublicKey recipient;
    private double amount;
    private byte[] parentTransactionID; // input -> TX -> Output

    public TransactionOutput(@NonNull PublicKey recipient, double amount, byte[] parentTransactionId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionID = parentTransactionId;
        byte[] dataToHash = Arrays.concatenate(
                this.recipient.getEncoded(),
                Utils.toByteArray(this.amount),
                this.parentTransactionID
        );

        this.ID = CryptoUtils.hash(dataToHash);
    }

    /**
     * Checks whether the specified public key matches the recipient of this transaction output.
     *
     * @param publicKey the public key to check against the recipient of this transaction output
     * @return true if the specified public key matches the recipient of this transaction output, false otherwise
     * @throws NullPointerException if the specified public key is null
     */
    public boolean isMine(@NonNull PublicKey publicKey) {
        return this.recipient.equals(publicKey);
    }

    public byte[] getBytes() {
        return Arrays.concatenate(
                this.ID,
                this.recipient.getEncoded(),
                Utils.toByteArray(this.amount),
                this.parentTransactionID
        );
    }
}
