package org.ssd.auction.transactions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A pool of pending transactions in a blockchain network.
 * <p>
 * A transaction pool is a collection of valid transactions that have not yet been included in a block.
 * <p>
 * This class provides a simple way to manage a pool of pending transactions in a network, and
 * to notify interested parties whenever new transactions are added to the pool.
 */
public class TransactionPool {
    private final List<Transaction> pendingTransactions;
    private final List<Consumer<Integer>> consumers; // == subscribers

    /**
     * Initializes the transaction pool with no pending transactions and no consumers.
     */
    public TransactionPool() {
        this.pendingTransactions = new ArrayList<>();
        this.consumers = new LinkedList<>();
    }

    public int getPoolSize() {
        return this.pendingTransactions.size();
    }

}
