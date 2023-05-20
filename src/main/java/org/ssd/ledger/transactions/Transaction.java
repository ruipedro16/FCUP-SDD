package org.ssd.ledger.transactions;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
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
    private byte[] id;
    private final PublicKey sender;
    private final PublicKey recipient;
    private final double amount;
    private byte[] signature;

    private final List<TransactionInput> txInputs;
    private final List<TransactionOutput> txOutputs;

    public Transaction(PublicKey sender, PublicKey recipient, double amount, List<TransactionInput> txInputs) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.txInputs = txInputs;
        this.txOutputs = new ArrayList<>();
        this.id = Transaction.computeTransactionID(this);
    }

    public static byte[] computeTransactionID(@NonNull Transaction transaction) {
        byte[] txInputBytes = null;
        if (transaction.getTxInputs() != null) {
            txInputBytes = Utils.toByteArray(
                    transaction.txInputs.stream()
                            .map(TransactionInput::getBytes)
                            .collect(Collectors.toList())
            );
        }

        byte[] txOutputBytes = Utils.toByteArray(
                transaction.txOutputs.stream()
                        .map(TransactionOutput::getBytes)
                        .collect(Collectors.toList())
        );

        byte[] tmp;
        if (txInputBytes == null) {
            tmp = txOutputBytes;
        } else {
            tmp = Arrays.concatenate(txInputBytes, txOutputBytes);
        }


        byte[] dataToHash = Arrays.concatenate(
                transaction.sender.getEncoded(),
                transaction.recipient.getEncoded(),
                Utils.toByteArray(transaction.amount),
                tmp
        );

        return CryptoUtils.hash(dataToHash);
    }

    public void setSignature(@NonNull PrivateKey privateKey) {
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

    /**
     * Validates a transaction by checking its signature and the validity of its inputs and outputs.
     * This method removes the transaction inputs from the UTXOs if they are valid.
     *
     * @return {@code true} if the transaction is valid, {@code false} otherwise.
     */
    public boolean validateTransaction() {
        // Check if the transaction signature is valid & Check if the inputs and outputs amounts match
        if (!verifySignature() || getInputsAmount() != getOutputsAmount()) {
            return false;
        }


        // Check if each input's unspent output exists and has the correct amount
        for (TransactionInput input : getTxInputs()) {
            TransactionOutput unspentOutput = DHT.getBlockchain().getUTXOs().get(ByteString.copyFrom(input.getTxOutputID()));
            if (unspentOutput == null || unspentOutput.getAmount() != input.getUnspentTxOutput().getAmount()) {
                return false;
            }

            // Remove the input's unspent output from the UTXOs
            DHT.getBlockchain().getUTXOs().remove(ByteString.copyFrom(input.getTxOutputID()));
        }

        // If all checks passed, the transaction is valid
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Transaction from [").append(Hex.toHexString(this.sender.getEncoded()))
                .append("] to [").append(Hex.toHexString(this.recipient.getEncoded()))
                .append("], value: ").append(this.amount).append('\n');
        return sb.toString();
    }
}
