package org.ssd.ledger.block;

import lombok.Data;
import lombok.NonNull;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.ledger.transactions.TransactionPool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Blockchain implements Serializable {
    private List<Block> blocks;

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

    public void replaceBlockchain(List<Block> blockchain) {
        this.setBlocks(blockchain);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.getBlocks().forEach(block -> {
            sb.append(block.toString()).append("\n");
        });
        return sb.toString();
    }
}
