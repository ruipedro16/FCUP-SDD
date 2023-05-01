package org.ssd;

import lombok.Getter;
import org.ssd.auction.AuctionService;
import org.ssd.ledger.BlockchainManager;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.p2p.Node;
import org.ssd.p2p.communication.CommunicationManager;
import org.ssd.p2p.grpc.KadServer;

public class DHT {
    private static Node node;

    @Getter
    private static KadServer server;

    @Getter
    private static AuctionService auctionsService;

    @Getter
    private static Blockchain blockchain;

    @Getter
    private static BlockchainManager blockchainManager;

    @Getter
    private static Wallet wallet;

    @Getter
    private static Consensus consensus;

    @Getter
    private static CommunicationManager communicationManager;

    public static void main(String[] args) {

    }
}
