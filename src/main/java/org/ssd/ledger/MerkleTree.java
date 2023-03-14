package org.ssd.ledger;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class MerkleTree {
    private MerkleNode root;
    private final List<MerkleNode> nodes;
    private final List<MerkleNode> leaves;

    public MerkleTree() {
        this.root = null;
        this.nodes = new ArrayList<>();
        this.leaves = new ArrayList<>();
    }

    public void addNode(@NonNull MerkleNode node) {
        this.nodes.add(node);
    }

    public void addLeaf(@NonNull MerkleNode leaf) {
        leaves.add(leaf);
    }

    /*
     * Returns the index of the node with the target hash
     * Returns -1 if not found
     */
    public int findHash(@NonNull List<MerkleNode> leaves, byte[] target){
        return IntStream.range(0, leaves.size())
                .filter(index -> Arrays.equals(leaves.get(index).getHash(), target))
                .findFirst()
                .orElse(-1);
    }
}
