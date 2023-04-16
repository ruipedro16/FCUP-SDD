package org.ssd.utils;

import lombok.NonNull;

import java.security.*;

public class CryptoUtils {
    /**
     * Generates a KeyPair using the RSA algorithm with a key size of 2048 bits.
     *
     * @return A KeyPair containing a public and private key.
     * @throws RuntimeException if RSA algorithm is not available.
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(2048, new SecureRandom());
            return keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Computes the SHA-1 hash of the input data.
     *
     * @param data the data to hash
     * @return the SHA-1 hash of the input data
     * @throws IllegalArgumentException if the input data is null
     * @throws RuntimeException         if SHA-1 algorithm is not available
     */
    public static byte[] hash(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a digital signature of the input data using the specified private key and the SHA-256 with RSA algorithm.
     *
     * @param privateKey the private key to sign the data
     * @param data       the data to be signed
     * @return a digital signature of the input data
     * @throws IllegalArgumentException if the input data is null
     * @throws RuntimeException         if the private key is invalid, the signature algorithm is not available, or a signature error occurs
     */
    public static byte[] generateSignature(@NonNull PrivateKey privateKey, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }

        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
        } catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the digital signature of the input data using the specified public key and the SHA-256 with RSA algorithm.
     *
     * @param publicKey the public key to verify the signature
     * @param signature the digital signature to be verified
     * @param data      the data that was signed
     * @return true if the signature is valid, false otherwise
     * @throws IllegalArgumentException if the signature or data is null
     * @throws RuntimeException         if the public key is invalid, the signature algorithm is not available, or a signature verification error occurs
     */
    public static boolean verifySignature(@NonNull PublicKey publicKey, byte[] signature, byte[] data) {
        if (signature == null || data == null) {
            throw new IllegalArgumentException();
        }
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
