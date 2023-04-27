package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.ledger.block.Block;

public record BlockMessage(Block block) implements Message {
}
