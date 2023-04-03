package org.ssd.auction.transactions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TransactionInput {
    private final byte[] txOutputID;
    private TransactionOutput unspentTxOutput;
}
