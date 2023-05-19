package org.ssd.ledger.transactions;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;

import java.io.Serializable;

@Data
@Getter
@RequiredArgsConstructor
public class TransactionInput implements Serializable {
    private final byte[] txOutputID;
    private TransactionOutput unspentTxOutput;

    public byte[] getBytes() {

        if (unspentTxOutput == null) {
            return this.txOutputID;
        }

        return Arrays.concatenate(
                this.txOutputID,
                this.unspentTxOutput.getBytes()
        );
    }
}
