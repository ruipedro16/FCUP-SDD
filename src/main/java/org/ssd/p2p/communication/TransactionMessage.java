package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.ledger.transactions.Transaction;

@AllArgsConstructor
@Data
public class TransactionMessage implements Message {
    private final MessageType messageType = MessageType.BROADCAST_TRANSACTION;
    private final Transaction transaction;
}
