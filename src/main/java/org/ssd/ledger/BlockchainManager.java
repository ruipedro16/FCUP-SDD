package org.ssd.ledger;

import lombok.NonNull;
import org.ssd.ledger.block.Block;

public interface BlockchainManager {
    void notifyNewBlock(@NonNull Block block);
}
