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
    // private static final Logger logger = LogManager.getLogger(Block.class);

    private long timestamp;
    private int nonce;
    private byte[] hash;
    private byte[] previousHash;
    private PublicKey validator; // in proof of stake (?)
    private List<Transaction> transactions;

    public Block(PublicKey validator, List<Transaction> transactions) {
        this.timestamp = System.currentTimeMillis();
        this.previousHash = null; // this is set in the Blockchain class
        this.validator = validator;
        this.transactions = transactions;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    protected static Block generateGensis() {
        Block genesis = new Block(null, null);
        genesis.setPreviousHash(null);
        // logger.debug("Generated genesis block");
        return genesis;
    }

    public byte[] calculateHash() {
        byte[] transactionBytes = null;
        byte[] dataToHash = null;

        if (this.transactions != null) {

            transactionBytes = Utils.toByteArray(transactions.stream()
                    .map(Transaction::getBytes)
                    .collect(Collectors.toList()));

            // TODO: include the previous hash(?)
            dataToHash = Arrays.concatenate(
                    Longs.toByteArray(timestamp),
                    Longs.toByteArray(nonce),
                    validator.getEncoded(),
                    transactionBytes
            );
        } else {
            dataToHash = Longs.toByteArray(timestamp);
        }

        return CryptoUtils.hash(dataToHash);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!Hex.toHexString(hash).substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        // logger.debug("Mined block " + Hex.toHexString(hash));
    }
}
