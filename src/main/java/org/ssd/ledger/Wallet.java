package org.ssd.ledger;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.NonNull;
import org.ssd.DHT;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionInput;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.utils.CryptoUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

@Data
public class Wallet {
    private byte[] id; // Hash of the public key
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Unspent transactions
     */
    private Map<ByteString, TransactionOutput> unspentTXOs;

    public Wallet() {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.id = CryptoUtils.hash(this.publicKey.getEncoded());
        this.unspentTXOs = new HashMap<>();
    }

    /*
     * Iterate over the unspent transactions of the blockchain
     * See if they belong to me
     * If so, add them to the unspentTXOs
     * Return the sum of my unspentTXOs
     */
    public double getBalance() {
        return DHT.getBlockchain().getUTXOs().values().stream().filter(utxo -> utxo.isMine(this.publicKey)).mapToDouble(utxo -> {
            this.unspentTXOs.put(ByteString.copyFrom(utxo.getID()), utxo); // add to the list of unspentTXOs
            return utxo.getAmount();
        }).sum();
    }

    public Transaction createTransaction(@NonNull PublicKey recipient, double amount) {
        // Check if the sender's balance is sufficient to make the transaction.
        double currentBalance = getBalance();
        if (currentBalance < amount) {
            System.out.println("Not enough funds for creating the transaction");
            return null;
        }

        // Find unspent transaction outputs (unspentTXOs) that can be used to fund the new transaction.
        List<TransactionInput> txInputs = new ArrayList<>();
        double total = 0;
        Iterator<TransactionOutput> utxoIterator = unspentTXOs.values().iterator();
        while (utxoIterator.hasNext() && total <= amount) { // stops adding inputs once the total value is greater than or equal to the transaction amount
            TransactionOutput utxo = utxoIterator.next();
            total += utxo.getAmount();
            txInputs.add(new TransactionInput(utxo.getID()));
        }

        // Create a new transaction object and sign it
        Transaction nTransaction = new Transaction(this.publicKey, recipient, amount, txInputs); // the ID is set in the constructor
        nTransaction.setSignature(this.privateKey);

        // If in PoS, we need to update the stake of the recipient and the sender
        if (DHT.getConsensus() == Consensus.PoS) {
            double recipientStake = DHT.getStakingManager().getValidatorStake(recipient);
            DHT.getStakingManager().setValidatorStake(recipient, recipientStake + amount);

            double senderStake = DHT.getStakingManager().getValidatorStake(this.publicKey);
            DHT.getStakingManager().setValidatorStake(this.publicKey, senderStake - amount);
        }

        // Update the UTXO lists to reflect the spent outputs and new outputs.
        txInputs.forEach(t -> unspentTXOs.remove(ByteString.copyFrom(t.getTxOutputID()))); // removes the spent unspentTXOs from the sender's UTXO list

        txInputs.forEach(input -> {
            TransactionOutput txOutput = DHT.getBlockchain().getUTXOs().get(ByteString.copyFrom(input.getTxOutputID()));
            input.setUnspentTxOutput(txOutput);
        });

        // Set the new unspentTXOs for the resulting transaction for the recipient for the sender
        double remainingAmount = nTransaction.getInputsAmount() - amount;
        nTransaction.setId(Transaction.computeTransactionID(nTransaction));
        nTransaction.getTxOutputs().add(new TransactionOutput(nTransaction.getRecipient(), amount, nTransaction.getId()));
        nTransaction.getTxOutputs().add(new TransactionOutput(nTransaction.getSender(), remainingAmount, nTransaction.getId()));

        // Add new unspentTXOs to the blockchain
        nTransaction.getTxOutputs().forEach(t -> DHT.getBlockchain().getUTXOs().put(ByteString.copyFrom(t.getID()), t));

        // Mark used inputs as spent
        txInputs.stream()
                .filter(t -> t.getUnspentTxOutput() != null)
                .forEach(t -> DHT.getBlockchain().getUTXOs().remove(ByteString.copyFrom(t.getUnspentTxOutput().getID())));

        // Add the new transaction to the transaction pool.
        DHT.getBlockchain().getTransactionPool().addTransaction(nTransaction);

        return nTransaction;
    }
}
