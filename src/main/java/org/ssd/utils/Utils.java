package org.ssd.utils;

import lombok.NonNull;
import org.ssd.ledger.transactions.Transaction;

import java.nio.ByteBuffer;
import java.util.List;

public class Utils {
    public static byte[] toByteArray(List<byte[]> bytes) {
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

    public static byte[] getMerkleRoot(@NonNull List<Transaction> transactions) {
        // TODO
        return null;
    }
}
