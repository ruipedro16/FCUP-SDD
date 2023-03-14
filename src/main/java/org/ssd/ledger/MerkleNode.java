package org.ssd.ledger;

import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.utils.CryptoUtils;

@Data
public class MerkleNode {
    private byte[] hash;
    private final MerkleNode leftNode;
    private final MerkleNode rightNode;
    private MerkleNode parent;

    public MerkleNode(byte[] hash) {
        this.hash = hash;
        this.leftNode = null;
        this.rightNode = null;
        this.parent = null;
    }

    public MerkleNode(@NonNull MerkleNode leftNode, MerkleNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.leftNode.parent = this;

        if (this.rightNode != null) {
            this.rightNode.parent = this;
            this.hash = computeHash();
        } else {
            this.hash = this.leftNode.getHash();
        }
    }

    private byte[] computeHash() {
        assert leftNode != null;
        assert rightNode != null;

        byte[] dataToHash = Arrays.concatenate(
                leftNode.getHash(),
                rightNode.getHash()
        );

        if (dataToHash != null) {
            return CryptoUtils.hash(dataToHash);
        }

        return null;
    }
}
