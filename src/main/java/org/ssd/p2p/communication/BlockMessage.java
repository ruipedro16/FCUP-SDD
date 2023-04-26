package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.ledger.block.Block;

@AllArgsConstructor
@Data
public class BlockMessage implements Message {
    private final MessageType messageType = MessageType.BROADCAST_BLOCK;
    private final Block block;
}
