package org.ssd.ledger.block;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.Data;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;

import java.security.PublicKey;

@Data
public class BlockHeader {
    private byte[] hash;
    private byte[] previousHash;
    private byte[] merkleRoot;
    private long timestamp;
    private int nonce; // this will be set in the miningworker
    private PublicKey validatorPK; // TODO: only in PoS, is null in PoW


    /*
     * For PoW
     * For PoS we will also require the Public Key of the Validator
     */
    public BlockHeader(byte[] previousHash) {
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = computeHash();
    }


    public byte[] computeHash() {
        byte[] dataToHash = Arrays.concatenate(
                previousHash,
                merkleRoot,
                Ints.toByteArray(this.nonce),
                Longs.toByteArray(this.timestamp)
        );

        return CryptoUtils.hash(dataToHash);
    }
}
