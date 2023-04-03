package org.ssd.ledger;

import com.google.common.primitives.Longs;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Block {
    private static final Logger logger = LogManager.getLogger(Block.class);

    private long timestamp;
    private int nonce;
    private byte[] hash;
    private byte[] previousHash;
    private PublicKey validator; // PoS : Validator, PoW : Miner
    private final Consensus consensus;
    // private List<Transaction> transactions;

    public Block(PublicKey validator, /* List<Transaction> transactions*/ Consensus consensus) {
        this.timestamp = System.currentTimeMillis();
        this.previousHash = null; // this is set in the Blockchain class
        this.validator = validator;
        // this.transactions = transactions;
        this.nonce = 0;
        this.consensus = consensus;
    }

    protected static Block generateGensis() {
        Block genesis = new Block(null, /* null,*/ Consensus.PoW);
        genesis.setPreviousHash(null);
        logger.debug("Generated genesis block");
        return genesis;
    }
}
