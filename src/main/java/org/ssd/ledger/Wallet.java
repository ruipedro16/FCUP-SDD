package org.ssd.ledger;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionInput;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.p2p.communication.MessageContent;
import org.ssd.p2p.communication.TransactionMessage;
import org.ssd.utils.CryptoUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/*
 * TODO: add function to create transactions
 */

@Data
public class Wallet {
    private byte[] id; // Hash of the public key
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Unspent transactions
     */
    private Map<byte[], TransactionOutput> UTXOs;

    public Wallet() {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.id = CryptoUtils.hash(this.publicKey.getEncoded());
        this.UTXOs = new HashMap<>();
    }

    /*
     * Iterate over the unspent transactions of the blockchain
     * See if they belong to me
     * If so, add them to the UTXOs
     * Return the sum of my UTXos
     */
    public double getBalance() {
        return DHT.getBlockchain().getUTXOs().values()
                .stream()
                .filter(utxo -> utxo.isMine(this.publicKey))
                .mapToDouble(utxo -> {
                    this.UTXOs.put(utxo.getID(), utxo); // add to the list of UTXOs
                    return utxo.getAmount();
                })
                .sum();
    }

    public Transaction createTransaction(@NonNull PublicKey recipient, double amount) {
        // Check if the sender's balance is sufficient to make the transaction.
        double currentBalance = getBalance();
        if (currentBalance < amount) {
            System.out.println("Not enough funds for creating the transaction");
            return null;
        }

        // Find unspent transaction outputs (UTXOs) that can be used to fund the transaction.
        List<TransactionInput> txInputs = new ArrayList<>();
        double total = 0;
        Iterator<TransactionOutput> utxoIterator = UTXOs.values().iterator();
        while(utxoIterator.hasNext() && total < amount) { // stops adding inputs once the total value is greater than or equal to the transaction amount
            TransactionOutput utxo = utxoIterator.next();
            total += utxo.getAmount();
            txInputs.add(new TransactionInput((utxo.getID())));
        }

        // Create a new transaction object and sign it
        Transaction newTransaction = new Transaction(this.publicKey, recipient, amount, txInputs); // the ID is set in the constructor
        newTransaction.generateSignature(this.privateKey);

        /*
         * Update the UTXO lists to reflect the spent outputs and new outputs.
         */
        txInputs.forEach(t -> UTXOs.remove(t.getTxOutputID())); // removes the spent UTXOs from the sender's UTXO list

        ///////////////////////////// TODO:

        // Set the new UTXOs for the resulting transaction for the recipient
        newTransaction.getTxOutputs().add(new TransactionOutput(newTransaction.getRecipient(), amount, newTransaction.getId()));

        // Set the new UTXOs for the resulting transaction for the recipient for the sender
        double remainingAmount = newTransaction.getInputsAmount() - amount;
        newTransaction.getTxOutputs().add(new TransactionOutput(newTransaction.getSender(), remainingAmount, newTransaction.getId()));

        // Mark used inputs as spent
        txInputs.stream()
                .filter(t-> t.getUnspentTxOutput() != null)
                .forEach(t -> DHT.getBlockchain().getUTXOs().remove(t.getUnspentTxOutput().getID()));

        // Add new UTXOs to the blockchain
        newTransaction.getTxOutputs().forEach(t -> DHT.getBlockchain().getUTXOs().put(t.getID(), t));

        // Add the new transaction to the transaction pool.
        DHT.getBlockchain().getTransactionPool().addTransaction(newTransaction);

        // Broadcast the new transaction to other nodes in the network.
        MessageContent txMessage = new TransactionMessage(newTransaction);
        DHT.getCommunicationManager().broadcastMessage(txMessage);

        return newTransaction;
    }


    @Override
    public String toString() {
        return Hex.toHexString(this.id);
    }
}
