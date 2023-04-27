package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.ledger.transactions.Transaction;

@AllArgsConstructor
@Data
public record TransactionMessage(Transaction transaction) implements Message {
}
