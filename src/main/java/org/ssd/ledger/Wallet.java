package org.ssd.ledger;

import lombok.Data;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.utils.CryptoUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Data
public class Wallet {
    private byte[] id;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Unspent transactions
     */
    private Map<String, TransactionOutput> UTXOs;

    public Wallet() {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.id = CryptoUtils.hash(this.publicKey.getEncoded());
        this.UTXOs = new HashMap<>();
    }

    /*
     * TODO: 
     */
    public double getBalance() {
        return 0f;
    }

    @Override
    public String toString() {
        return Hex.toHexString(this.id);
    }
}
