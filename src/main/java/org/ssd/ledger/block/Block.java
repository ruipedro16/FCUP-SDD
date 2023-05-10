package org.ssd.ledger.block;

import lombok.Data;
import lombok.NonNull;
import org.ssd.DHT;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.transactions.Transaction;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Data
public class Block implements Cloneable {
    private final BlockHeader header;
    private final List<Transaction> transactions;

    public Block(byte[] previousHash) {
        if (previousHash == null) {
            throw new IllegalArgumentException();
        }

        this.header = new BlockHeader(previousHash);
        this.transactions = new ArrayList<>();
    }

    public void setValidator(@NonNull PublicKey publicKey) {
        assert DHT.getConsensus().equals(Consensus.PoS);

        this.header.setValidatorPK(publicKey);
    }

    public byte[] getBlockHash() {
        return this.header.getHash();
    }

    public void addTransactions(@NonNull List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            if (transaction == null) {
                System.out.println("Transaction failed to process. Ignored.");
                return;
            }

            if (header.getPreviousHash() != null) { // the hash of the genesis block is `null`
                if ((!transaction.verifySignature())) {
                    System.out.println("Transaction Signature failed to verify. Ignored.");
                    return;
                }
            }

            transactions.add(transaction);
            System.out.println("Transaction Successfully added to the block");
        });
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
