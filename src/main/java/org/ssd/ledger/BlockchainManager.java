package org.ssd.ledger;

import lombok.NonNull;
import org.ssd.ledger.block.Block;

import java.util.function.Consumer;

public interface BlockchainManager {
    void registerBlockConsumer(@NonNull Consumer<Block> consumer);

    void notifyNewBlock(@NonNull Block block);
}
