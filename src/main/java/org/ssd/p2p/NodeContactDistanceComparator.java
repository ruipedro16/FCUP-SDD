package org.ssd.p2p;

import org.ssd.utils.Utils;

import java.math.BigInteger;
import java.util.Comparator;

public class NodeContactDistanceComparator implements Comparator<NodeContact> {
    private final byte[] key;

    public NodeContactDistanceComparator(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    @Override
    public int compare(NodeContact n1, NodeContact n2) {
        byte[] distance1 = Node.getDistance(n1.getId(), key);
        byte[] distance2 = Node.getDistance(n2.getId(), key);
        return new BigInteger(distance1).compareTo(new BigInteger(distance2));
    }
}
