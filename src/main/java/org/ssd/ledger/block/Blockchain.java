package org.ssd.ledger.block;

import lombok.Data;
import lombok.NonNull;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.ledger.transactions.TransactionPool;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Blockchain implements Serializable {
    private final List<Block> blocks;

    /*
     * Pending transactions that will be included in new blocks
     */
    private final TransactionPool transactionPool;

    private final Map<byte[], TransactionOutput> UTXOs;

    public Blockchain() {
        this.blocks = new ArrayList<>();
        Block genesis = new Block(null); // the previousHash of the genesis block is NULL
        // The genesis block does not contain any transactions
        this.blocks.add(genesis);
        this.transactionPool = new TransactionPool();
        this.UTXOs = new HashMap<>();
    }

    public void addBlock(@NonNull Block block) {
        this.blocks.add(block);
    }

    public Block getLastBlock() {

        return this.blocks.get(this.blocks.size() - 1);
    }

    public List<Transaction> getAllTransactions() {
        return this.blocks.stream()
                .map(Block::getTransactions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public double getValidatorStake(@NonNull PublicKey validatorPK) {
        return this.UTXOs.values()
                .stream()
                .filter(utxo -> utxo.isMine(validatorPK))
                .mapToDouble(utxo -> {
                    this.UTXOs.put(utxo.getID(), utxo); // add to the list of UTXOs
                    return utxo.getAmount();
                })
                .sum();
    }
}
