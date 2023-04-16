package org.ssd;

import lombok.Getter;
import org.ssd.auction.AuctionsService;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.p2p.Node;
import org.ssd.p2p.grpc.KadServer;

public class DHT {
    //node
    private static Node node;

    //server
    @Getter
    private static KadServer server;

    //auctions
    @Getter
    private static AuctionsService auctionsService;
    // private InetAddress bootstrapNodeAddress; TODO: Nao é preciso: basta estarno construtor

    @Getter
    private static Blockchain blockchain;

    @Getter
    private static MiningManager miningManager;

    @Getter
    private static StakingManager stakingManager;

    @Getter
    private static Wallet wallet;
}
