package org.ssd.ledger.staking;

import lombok.NonNull;
import org.ssd.DHT;
import org.ssd.constants.BlockchainConstants;
import org.ssd.ledger.BlockchainManager;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.block.Block;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionPool;

import java.security.PublicKey;
import java.util.*;
import java.util.function.Consumer;

/**
 * cf. MiningManager => Same but for PoS
 */
public class StakingManager implements BlockchainManager {
    private final Blockchain blockchain;
    private final TransactionPool transactionPool;
    private final List<Consumer<Block>> consumers;

    /**
     * A map of public keys of validators to their respective stake amount.
     */
    private final Map<PublicKey, Double> validators;
    private boolean running;

    public StakingManager(@NonNull Blockchain blockchain) {
        assert DHT.getConsensus().equals(Consensus.PoS);

        this.blockchain = blockchain;
        this.transactionPool = blockchain.getTransactionPool();
        this.consumers = new ArrayList<>();
        this.validators = new HashMap<>();
        this.running = false;

        if (transactionPool != null) {
            this.transactionPool.registerSubscriber(this::handleNewTransaction);
        }
    }

    @Override
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
    private void handleNewTransaction(int nTransactions) {
        if (transactionPool.getPoolSize() >= BlockchainConstants.MIN_N_TRANSACTIONS && !this.running) {
            System.out.println("Enough transactions in pool.");
            this.running = true;

            int n = Math.min(transactionPool.getPoolSize(), BlockchainConstants.MAX_N_TRANSACTIONS);
            LinkedList<Transaction> newTransactions = transactionPool.getTransactions(n);

            // Select a validator for the block
            PublicKey validatorPK = selectValidator();
            Block newBlock = new Block(this.blockchain.getLastBlock().getHeader().getHash());
            newBlock.addTransactions(newTransactions); // transactions are verified here
            newBlock.setValidator(validatorPK);
        }
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
     * <p>
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

        // sortedValidators contains the same entries as validators, but sorted by stake (in descending order)
        // highest stake first
        TreeMap<PublicKey, Double> sortedValidators = new TreeMap<>(
                new Comparator<PublicKey>() {
                    @Override
                    public int compare(@NonNull PublicKey publicKey1, @NonNull PublicKey publicKey2) {
                        Double stake1 = validators.get(publicKey1);
                        Double stake2 = validators.get(publicKey2);
                        return stake2.compareTo(stake1); // Sort in descending order
                    }
                });
        sortedValidators.putAll(validators); // Copy entries from HashMap to TreeMap

        // generates a random number between 0 (inclusive) and totalStakedAmount (exclusive)
        double r = Math.random() * totalStakedAmount;
        double sum = 0;
        for (Map.Entry<PublicKey, Double> entry : sortedValidators.entrySet()) {
            sum += entry.getValue();
            if (r <= sum) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public void notifyNewBlock(@NonNull Block block) {
        this.blockchain.addBlock(block);
        this.consumers.forEach(consumer -> consumer.accept(block));
    }
}
