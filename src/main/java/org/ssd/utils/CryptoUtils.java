package org.ssd.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class CryptoUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateSignature(PrivateKey privateKey, byte[] data)  {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifySignature(PublicKey publicKey, byte[] signature, byte[] data) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
