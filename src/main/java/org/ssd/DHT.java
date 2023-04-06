package org.ssd;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.auction.AuctionsService;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.p2p.Node;
import org.ssd.p2p.grpc.GrpcServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@NoArgsConstructor
public class DHT {
    private static final Logger logger = LogManager.getLogger(DHT.class);

    //default port, use 80 for now
    public static final int PORT = 80;

    //node
    private Node node;
    //server
    private GrpcServer server;

    //auctions
    @Getter
    private static AuctionsService auctionsService;
    private InetAddress bootstrapNodeAddress;

    @Getter
    private static Blockchain blockchain;

    @Getter
    private static MiningManager miningManager;

    @Getter
    private static StakingManager stakingManager;

    @Getter
    private static Wallet wallet;

    public DHT(@NonNull InetAddress bootstrapNodeAddress, @NonNull Consensus consensus) throws IOException {
        this.server = new GrpcServer();
        //if node id is provided at startup then use a different init
        this.node = this.server.autoInit(PORT); //defaults

        //  this.auctionsService = new AuctionsService(this.node); // Moved to initAuctionService
        //add option for bootstrap
        this.bootstrapNodeAddress = bootstrapNodeAddress;


        initBlockchain(consensus);
        initAuctionService();

        DHT.wallet = new Wallet();

        demoTransactions();
    }

    private void initBlockchain(@NonNull Consensus consensus) {
        DHT.blockchain = new Blockchain();

        switch (consensus) {
            case PoW -> {
                DHT.stakingManager = null;
                DHT.miningManager = new MiningManager(DHT.blockchain);
            }

            case PoS -> {
                DHT.stakingManager = new StakingManager();
                DHT.miningManager = null;
            }
        }

        System.out.println("Initialized the blockchain");
    }

    private void initAuctionService() {
        // TODO:
        DHT.auctionsService = new AuctionsService(this.node); // ???

        System.out.println("Initialized the AuctionService");
    }

    private void demoTransactions() {
        // Create transactions for demonstration purposes
    }

    /*
     * args[0]: Address of one of the bootstrap nodes
     * args[1]: Consensus mechanism: PoW or PoS
     *
     * Convêm haver > 1 bootstrap nodes para evitar CPoF
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage:\nargs[0]: Address of a bootstrap node\nargs[1]: Consensus mechanism: PoW or PoS");
            System.exit(1);
            return;
        }

        InetAddress bootstrapNodeAddress;
        Consensus consensus;

        try {
            bootstrapNodeAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + args[0], e);
        }

        switch (args[1]) {
            case "PoW" -> consensus = Consensus.PoW;
            case "PoS" -> consensus = Consensus.PoS;
            default -> throw new IllegalArgumentException();
        }

        DHT dht = new DHT(bootstrapNodeAddress, consensus);
    }
}