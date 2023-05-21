package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.ledger.block.Block;

import java.util.List;

@AllArgsConstructor
@Getter
public class BlockchainMessage extends Message {

    @NonNull
    private final List<Block> blockchain;

    @Override
    public MessageClass messageClass() {
        return MessageClass.BLOCKCHAIN_MESSAGE;
    }
}
