package org.ssd.utils;

import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.ssd.ledger.transactions.Transaction;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static byte[] serializeObject(@NonNull Serializable object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        bos.close();
        return bos.toByteArray();
    }

    public static Object deserializeBytes(byte @NonNull [] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bos = new ByteArrayInputStream(data);
        ObjectInputStream oos = new ObjectInputStream(bos);
        return oos.readObject();
    }

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

    public static InetAddress getLocalHostAddress() {
        InetAddress address = null;

        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return address;
    }

    public static String getLocalHostStringAddress(@NonNull InetAddress address) {
        return address.getHostAddress();
    }

    public static InetAddress getAddressFromString(@NonNull String address) {
        InetAddress res = null;

        try {
            res = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
