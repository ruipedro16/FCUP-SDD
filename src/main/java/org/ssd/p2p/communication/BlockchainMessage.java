package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.ssd.ledger.block.Blockchain;

@AllArgsConstructor
public class BlockchainMessage extends Message {

    @NonNull
    private final Blockchain blockchain;

    @Override
    public MessageClass messageClass() {
        return MessageClass.BLOCK_MESSAGE;
    }
}
