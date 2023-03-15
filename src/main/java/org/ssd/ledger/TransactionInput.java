package org.ssd.ledger;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class TransactionInput {
    private final byte[] transactionOutputID;

    /*
     * UTXO: Unspent Transaction Output
     *       Refers to an output of a previous transaction in the blockchain that has not been spent yet.
     *
     * Each transaction input refers to a previous UTXO that the transaction is spending, and each output creates new UTXOs.
     * When a transaction output is spent by a subsequent transaction, the UTXO is considered to be "consumed" and can
     * no longer be used as an input in any further transactions.
     *
     *
     * In order to prevent double-spending, nodes keep track of all UTXOs in the blockchain and verify that each transaction
     * input is referencing a valid and unspent UTXO. This helps ensure that the same UTXO is not used as an input in
     * multiple transactions.
     */
    private TransactionOutput utxo;

    public TransactionInput(byte[] transactionOutputID) {
        this.transactionOutputID = transactionOutputID;
    }
}
