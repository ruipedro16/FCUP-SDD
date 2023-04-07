package org.ssd.ledger.staking;

import lombok.NonNull;
import org.ssd.constants.BlockchainConstants;
import org.ssd.ledger.block.Block;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionPool;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Consumer;

/**
 * cf. MiningManager => Same but for PoS
 */
public class StakingManager {
    private final Blockchain blockchain;
    private final TransactionPool transactionPool;
    private final List<Consumer<Block>> consumers;

    /**
     * A map of public keys of validators to their respective stake amount.
     */
    private final Map<PublicKey, Double> validators; // TODO: Double ou Long ?
    private boolean running;

    public StakingManager(@NonNull Blockchain blockchain) {
        this.blockchain = blockchain;
        this.transactionPool = blockchain.getTransactionPool();
        this.consumers = new ArrayList<>();
        this.validators = new HashMap<>();
        this.running = false;

        if (transactionPool != null) {
            this.transactionPool.registerSubscriber(this::handleNewTransaction);
        }
    }

    public void registerBlockConsumer(@NonNull Consumer<Block> consumer) {
        this.consumers.add(consumer);
    }

    public void registerValidator(@NonNull PublicKey publicKey, double stakedAmount) {
        this.validators.put(publicKey, stakedAmount);
    }

    /**
     * Handles a new transaction from the transaction pool.
     *
     * @param nTransactions The number of new transactions in the pool.
     */
    public void handleNewTransaction(int nTransactions) {
        if (transactionPool.getPoolSize() >= BlockchainConstants.MIN_N_TRANSACTIONS && !this.running) {
            System.out.println("Enough transactions in pool.");
            this.running = true;

            int n = Math.min(transactionPool.getPoolSize(), BlockchainConstants.MAX_N_TRANSACTIONS);
            LinkedList<Transaction> newTransactions = transactionPool.getTransactions(n);

            // Select a validator for the block
            PublicKey validator = selectValidator();

            // TODO: verify the signature of the transactions
        }
    }

    public void shutDownStakingWorker() {
        this.running = false;
    }

    /**
     * Calculates the total staked amount of all validators.
     */
    private Double getTotalStakedAmount() {
        return this.validators.values().stream()
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Selects a validator using a proof-of-stake algorithm based on the total amount staked by each validator.
     *
     * This function randomly selects a validator from the validators map, with the probability of each validator being
     * selected proportional to their staked amount.
     *
     * @return the public key of the selected validator, or null if no validators exist
     */
    public PublicKey selectValidator() {

        double totalStakedAmount = getTotalStakedAmount();

        // If totalStakedAmount == 0, there are no validators to choose from
        if (totalStakedAmount == 0) {
            return null;
        }

        // generates a random number between 0 (inclusive) and totalStakedAmount (exclusive)
        double r = Math.random() * totalStakedAmount;
        double sum = 0;
        for (Map.Entry<PublicKey, Double> entry : validators.entrySet()) {
            sum += entry.getValue();
            if (r <= sum) {
                return entry.getKey();
            }
        }

        return null;
    }


}
