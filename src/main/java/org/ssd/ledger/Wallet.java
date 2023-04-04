package org.ssd.ledger;

import lombok.Data;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.utils.CryptoUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Data
public class Wallet {
    private byte[] id; // Hash of the public key
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Unspent transactions
     */
    private Map<byte[], TransactionOutput> UTXOs;

    public Wallet() {
        KeyPair keyPair = CryptoUtils.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.id = CryptoUtils.hash(this.publicKey.getEncoded());
        this.UTXOs = new HashMap<>();
    }

    /*
     * Iterate over the unspent transactions of the blockchain
     * See if they belong to me
     * If so, add them to the UTXOs
     * Return the sum of my UTXos
     */
    public double getBalance() {
        return DHT.getBlockchain().getUTXOs().values()
                .stream()
                .filter(utxo -> utxo.isMine(this.publicKey))
                .mapToDouble(utxo -> {
                    this.UTXOs.put(utxo.getID(), utxo); // add to the list of UTXOs
                    return utxo.getAmount();
                })
                .sum();
    }

    @Override
    public String toString() {
        return Hex.toHexString(this.id);
    }
}
