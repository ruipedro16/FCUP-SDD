package org.ssd.ledger.transactions;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;

@Data
@Getter
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
