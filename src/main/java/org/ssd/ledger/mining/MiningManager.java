package org.ssd.ledger.mining;

import lombok.Data;
import lombok.NonNull;
import org.ssd.constants.BlockchainConstants;
import org.ssd.ledger.block.Block;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionPool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is responsible for managing the mining process, registering subscribers to the transaction pool,
 * creating a miner worker to mine new blocks when the transaction pool has enough transactions.
 * It also notifies subscribed consumers when a block has been mined.
 */

@Data
public class MiningManager {
    private final Blockchain blockchain;
    private final TransactionPool transactionPool;
    private MiningWorker miningWorker;
    private boolean running;

    /*
     * Subscribers to be notified when the block is mined
     */
    private final List<Consumer<Block>> consumers;

    public MiningManager(@NonNull Blockchain blockchain) {
        this.blockchain = blockchain;
        this.transactionPool = blockchain.getTransactionPool();

        if (transactionPool != null) {
            transactionPool.registerSubscriber(this::handleNewTransaction);
        }

        this.consumers = new ArrayList<>();
    }

    public void registerBlockConsumer(@NonNull Consumer<Block> consumer) {
        this.consumers.add(consumer);
    }

    /**
     * Handles a new transaction from the transaction pool.
     *
     @param nTransactions The number of new transactions in the pool.
     */
    private void handleNewTransaction(int nTransactions) {
        if (transactionPool.getPoolSize() >= BlockchainConstants.MIN_N_TRANSACTIONS && !this.running) {
            shutDownMinerWorker();

            System.out.println("Enough transactions in pool, starting to mine.");
            this.running = true;

            int n = Math.min(transactionPool.getPoolSize(), BlockchainConstants.MAX_N_TRANSACTIONS);
            LinkedList<Transaction> newTransactions = transactionPool.getTransactions(n);

            Block blockToMine = null;

            // TODO: verificar isto; this.blockchain.getLastBlock() == null é sempre falso
            if (this.blockchain.getLastBlock() == null) { // If the blockchain is empty, we mine the genesis block
                blockToMine = new Block(null); // the hash of the genesis block is set to null
            } else {
                blockToMine = new Block(this.blockchain.getLastBlock().getHeader().getHash());
            }

            blockToMine.addTransactions(newTransactions);
            this.miningWorker = new MiningWorker(this, blockToMine);
            this.miningWorker.start();
        }
    }

    public void shutDownMinerWorker() {
        if (this.miningWorker != null) {
            this.miningWorker.interrupt();
        }

        this.running = false;
    }

    public void notifyMinedBlock(@NonNull Block block) {
        this.blockchain.addBlock(block);
        this.consumers.forEach(consumer -> consumer.accept(block));
    }
}
