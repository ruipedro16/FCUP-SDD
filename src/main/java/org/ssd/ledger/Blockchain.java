package org.ssd.ledger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Blockchain {
    // private static final Logger logger = LogManager.getLogger(Blockchain.class);

    private static final int DIFFICULTY = 4;

    private final List<Block> blocks;

    public Blockchain() {
        this.blocks = new ArrayList<>();

        blocks.add(Block.generateGensis());
        // logger.debug("Initialized the blockchain");
    }

    public Block getLastBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public void addBlock(Block block) {
        block.setPreviousHash(getLastBlock().getHash());
        block.mineBlock(DIFFICULTY);
        blocks.add(block);
        // logger.debug("Added block " + Hex.toHexString(block.getHash()));
    }

    public boolean isValid() {
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            // Check if the current block's hash is correct
            // Check if the previous block's hash is correct
            if (!Arrays.equals(currentBlock.getHash(), currentBlock.calculateHash()) ||
                    !Arrays.equals(previousBlock.getHash(), currentBlock.getPreviousHash())) {
                return false;
            }
        }

        return true;
    }
}
