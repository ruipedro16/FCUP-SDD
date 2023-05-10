package org.ssd.ledger.block;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.Data;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;

import java.security.PublicKey;
import java.util.Optional;

@Data
public class BlockHeader {
    private byte[] hash;
    private final byte[] previousHash;
    private byte[] merkleRoot;
    private long timestamp;
    private int nonce;
    private PublicKey validatorPK; // only in PoS, is null in PoW

    public BlockHeader(byte[] previousHash) {
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = computeHash();
        this.validatorPK = null;
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
