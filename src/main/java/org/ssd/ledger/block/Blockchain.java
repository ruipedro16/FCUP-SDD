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
    private final List<Block> blocks;

    /*
     * Pending transactions that will be included in new blocks
     */
    private final TransactionPool transactionPool;

    private final Map<byte[], TransactionOutput> UTXOs;

    public Blockchain() {
        this.blocks = new ArrayList<>();
        this.transactionPool = new TransactionPool();
        this.UTXOs = new HashMap<>();
    }

    public void addBlock(@NonNull Block block) {
        this.blocks.add(block);
    }

    public Block getLastBlock() {
        if (this.blocks.size() > 0) {
            return this.blocks.get(this.blocks.size() - 1);
        }

        return null;
    }
}
