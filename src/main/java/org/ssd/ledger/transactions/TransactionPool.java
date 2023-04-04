package org.ssd.ledger.transactions;

import lombok.NonNull;

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

    /**
     * Adds a new transaction to the pool and notifies all consumers of the new transaction count.
     *
     * @param transaction the transaction to add to the pool
     * @throws NullPointerException if the specified transaction is null
     */
    public void addTransaction(@NonNull Transaction transaction) {
        this.pendingTransactions.add(transaction);
        this.consumers.forEach(consumer -> consumer.accept(pendingTransactions.size()));
    }

    /**
     * Registers a new subscriber to be notified of changes to the transaction pool.
     *
     * @param consumer the consumer to register as a subscriber
     * @throws NullPointerException if the specified consumer is null
     */
    public void registerSubscriber(@NonNull Consumer<Integer> consumer) {
        this.consumers.add(consumer);
    }

    /**
     * Gets n transactions from the pool
     *
     * @param n
     * @return
     */
    public LinkedList<Transaction> getTransactions(int n) {
        assert (n > 0);

        LinkedList<Transaction> transactionsToRemove = new LinkedList<>();
        for (int i = 0; i < n && !pendingTransactions.isEmpty(); i++) {
            transactionsToRemove.add(pendingTransactions.remove(0));
        }
        return transactionsToRemove;
    }
}
