package org.ssd.utils;

import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.ledger.transactions.Transaction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static byte[] toByteArray(@NonNull List<byte[]> bytes) {
        int totalLength = 0;
        for (byte[] array : bytes) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : bytes) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }

    public static byte[] toByteArray(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

    public static byte[] toByteArray(int value) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
    }

    public static byte[] getMerkleRoot(@NonNull List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return null;
        }

        List<byte[]> treeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            treeLayer.add(transaction.getId());
        }
        while (treeLayer.size() > 1) {
            List<byte[]> previousTreeLayer = treeLayer;
            treeLayer = new ArrayList<>();
            for (int i = 0; i < previousTreeLayer.size(); i += 2) {
                byte[] left = previousTreeLayer.get(i);
                byte[] right = (i + 1 < previousTreeLayer.size()) ? previousTreeLayer.get(i + 1) : left;
                treeLayer.add(CryptoUtils.hash(Arrays.concatenate(left, right)));
            }
        }

        return treeLayer.get(0);
    }
}
