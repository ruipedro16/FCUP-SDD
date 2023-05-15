package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.ledger.transactions.Transaction;

@AllArgsConstructor
@Getter
public class TransactionMessage extends Message {

    @NonNull
    private final Transaction transaction;

    /**
     * @return Transaction type identifier
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.TRANSACTION_MESSAGE;
    }
}
