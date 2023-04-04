package org.ssd.ledger.mining;

import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.constants.BlockchainConstants;
import org.ssd.ledger.block.Block;
import org.ssd.utils.Utils;

/**
 * The MinerWorker class represents a worker thread responsible for mining a given block.
 * It continuously tries to find a valid nonce for the block header that satisfies the mining difficulty target.
 */
public class MiningWorker extends Thread {

    /**
     * The MiningManager instance that created this worker.
     */
    private final MiningManager miningManager;

    /**
     * The block to be mined. A clone of the original block is used to avoid modifying the original.
     */
    private Block block;

    public MiningWorker(@NonNull MiningManager manager, @NonNull Block block) {
        this.miningManager = manager;
        try {
            this.block = (Block) block.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the mining process on this worker thread.
     * It continuously tries to find a valid nonce for the block header that satisfies the mining difficulty target.
     */
    @Override
    public void run() {
        byte[] merkleRoot = Utils.getMerkleRoot(this.block.getTransactions());
        this.block.getHeader().setMerkleRoot(merkleRoot);

        // Proof of work
        String target = new String(new char[BlockchainConstants.MINING_DIFFICULTY]).replace('\0', '0');
        while(!Hex.toHexString(this.block.getBlockHash()).substring(0, BlockchainConstants.MINING_DIFFICULTY).equals(target)) {
            if (isInterrupted()) {
                break;
            }

            /*
             * Increment the nonce and update the block header hash until a valid hash is found.
             */
            int nonce = block.getHeader().getNonce();
            nonce++;
            block.getHeader().setNonce(nonce);
            byte[] updatedHash = this.block.getHeader().computeHash();
            this.block.getHeader().setHash(updatedHash);
        }

        if (!isInterrupted()) {
            System.out.println("Block mined: " + Hex.toHexString(block.getBlockHash()));
            this.miningManager.notifyMinedBlock(block);
        }
    }
}

