package org.ssd.ledger.transactions;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class Transaction implements Serializable {
    private final byte[] id;
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double amount;
    private byte[] signature;

    private final List<TransactionInput> txInputs;
    private final List<TransactionOutput> txOutputs;

    public Transaction(@NonNull PublicKey sender, @NonNull PublicKey recipient, double amount, List<TransactionInput> txInputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.txInputs = txInputs;
        this.txOutputs = new ArrayList<>();
        this.id = Transaction.computeTransactionID(this);
    }

    public static byte[] computeTransactionID(@NonNull Transaction transaction) {
        byte[] txInputBytes = Utils.toByteArray(
            transaction.txInputs.stream()
                    .map(TransactionInput::getBytes)
                    .collect(Collectors.toList())
        );

        byte[] txOutputBytes = Utils.toByteArray(
            transaction.txOutputs.stream()
                    .map(TransactionOutput::getBytes)
                    .collect(Collectors.toList())
        );

        byte[] tmp = Arrays.concatenate(txInputBytes, txOutputBytes);

        byte[] dataToHash = Arrays.concatenate(
                transaction.sender.getEncoded(),
                transaction.recipient.getEncoded(),
                Utils.toByteArray(transaction.amount),
                tmp
        );

        return CryptoUtils.hash(dataToHash);
    }

    public static boolean checkTransactionID(@NonNull Transaction transaction) {
        return java.util.Arrays.equals(transaction.getId(), Transaction.computeTransactionID(transaction));
    }

    public void generateSignature(@NonNull PrivateKey privateKey) {
        byte[] data = Arrays.concatenate(
                this.sender.getEncoded(),
                this.recipient.getEncoded(),
                Utils.toByteArray(this.amount)
        );

        this.signature = CryptoUtils.generateSignature(privateKey, data);
    }

    public boolean verifySignature() {
        byte[] data = Arrays.concatenate(
                this.sender.getEncoded(),
                this.recipient.getEncoded(),
                Utils.toByteArray(this.amount)
        );

        return CryptoUtils.verifySignature(this.sender, this.signature, data);
    }

    /*
     * Calculate the total amount of inputs for a transaction by iterating over the list of inputs and adding the amounts
     * of their unspent transaction outputs.
     */
    public double getInputsAmount() {
        return this.txInputs
                .stream()
                .filter(Objects::nonNull)
                .map(TransactionInput::getUnspentTxOutput)
                .mapToDouble(TransactionOutput::getAmount)
                .sum();
    }

    public double getOutputsAmount() {
        return this.txOutputs
                .stream()
                .mapToDouble(TransactionOutput::getAmount)
                .sum();
    }
}
