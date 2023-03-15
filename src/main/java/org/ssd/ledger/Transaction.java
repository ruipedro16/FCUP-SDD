package org.ssd.ledger;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.ledger.blockchain.Blockchain;

import java.security.PublicKey;
import java.util.List;
import java.util.Objects;

@Data
public class Transaction {
    private static final Logger logger = LogManager.getLogger(Transaction.class);

    private byte[] transactionID;
    private final PublicKey sender;
    private final PublicKey receiver;
    private final double amount;
    private byte[] signature;

    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs;

    private Blockchain blockchain;

    public Transaction(PublicKey sender, PublicKey receiver, double amount, List<TransactionInput> inputs) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.inputs = inputs;
    }

    private byte[] computeHash() {
        // todo
        return null;
    }

    public double getInputValues() {
        return this.inputs.stream()
                .map(TransactionInput::getUtxo)
                .filter(Objects::nonNull)
                .mapToDouble(TransactionOutput::getAmount)
                .sum();
    }

    public double getOutputValues() {
        return this.outputs.stream()
                .mapToDouble(TransactionOutput::getAmount)
                .sum();
    }

    public byte[] sign() {
        // todo
        return null;
    }

    public boolean verifySignature() {
        // todo:
        return true;
    }

    public boolean processTransaction() throws Exception {
        if (!verifySignature()) {
            logger.error("Invalid signature for transaction " + Hex.toHexString(this.transactionID));
        }

        // validate that inputs are not spent

        // generate outputs

        return true;
    }
}
