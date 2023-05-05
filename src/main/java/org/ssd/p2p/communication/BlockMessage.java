package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.ledger.block.Block;

@AllArgsConstructor
@Getter
public class BlockMessage extends MessageContent {

    @NonNull
    private final Block block;

    /**
     * @return Block message type identifier
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.BLOCK_MESSAGE;
    }
}
