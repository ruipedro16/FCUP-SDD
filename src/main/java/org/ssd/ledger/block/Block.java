package org.ssd.ledger.block;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.transactions.Transaction;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Data
public class Block implements Cloneable, Serializable {
    private final BlockHeader header;
    private final List<Transaction> transactions;

    public Block(byte[] previousHash) {
        // NOTE: for the genesis block, previousHash is NULL

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

            if (header.getPreviousHash() != null) { // the hash of the genesis block is null
                if ((!transaction.verifySignature())) {
                    System.out.println("Transaction signature failed to verify. Ignored.");
                    return;
                }
            }

            System.out.println("Transaction successfully added to the block");
        });
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n   PrevHash: ").append(Hex.toHexString(this.getHeader().getPreviousHash()))
                .append("\n   Hash: ").append(Hex.toHexString(this.getHeader().getHash()))
                .append("\n   Time: ").append(this.getHeader().getTimestamp())
                .append("\n   Nonce: ").append(this.getHeader().getNonce());
        if (this.getHeader().getValidatorPK() != null) sb.append("\n   ValidatorPK: ").append(Hex.toHexString(this.getHeader().getValidatorPK().getEncoded()));
        sb.append("\n}");
        return sb.toString();
    }
}
