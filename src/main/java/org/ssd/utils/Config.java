package org.ssd.utils;

import lombok.Getter;
import lombok.Setter;
import org.ssd.p2p.Consensus;

public class Config {
    @Getter
    @Setter
    private static Consensus consensus = null; // args[1]
}
