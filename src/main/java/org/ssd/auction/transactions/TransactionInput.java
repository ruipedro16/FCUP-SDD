package org.ssd.auction.transactions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;

@Data
@RequiredArgsConstructor
public class TransactionInput {
    private final byte[] txOutputID;
    private TransactionOutput unspentTxOutput;

    public byte[] getBytes() {
        return Arrays.concatenate(
                this.txOutputID,
                this.unspentTxOutput.getBytes()
        );
    }
}
