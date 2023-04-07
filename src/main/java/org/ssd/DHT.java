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

public class DHT {
    //default port, use 80 for now
    public static final int PORT = 80; // TODO: Remove this: port is set in args[1]

    //node
    private static Node node;

    //server
    @Getter
    private static GrpcServer server;

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

    public DHT(@NonNull InetAddress bootstrapNodeAddress, @NonNull Consensus consensus) throws IOException {
        DHT.server = new GrpcServer();
        //if node id is provided at startup then use a different init
        DHT.node = DHT.server.autoInit(PORT); //defaults

        //  this.auctionsService = new AuctionsService(this.node); // Moved to initAuctionService
        //add option for bootstrap
        // this.bootstrapNodeAddress = bootstrapNodeAddress;

        initBlockchain(consensus);
        initAuctionService();
        DHT.wallet = new Wallet();
        demoTransactions();
    }

    private void initBlockchain(@NonNull Consensus consensus) {
        DHT.blockchain = new Blockchain();
        System.out.println("Initialized the blockchain");

        switch (consensus) {
            case PoW -> {
                DHT.stakingManager = null;
                DHT.miningManager = new MiningManager(DHT.blockchain);
                System.out.println("Initialized the Mining Manager");
            }

            case PoS -> {
                DHT.stakingManager = new StakingManager(DHT.blockchain);
                DHT.miningManager = null;
                System.out.println("Initialized the Staking Manager");
            }
        }
    }

    private void initAuctionService() {
        // TODO:
        DHT.auctionsService = new AuctionsService(DHT.node); // ???
        System.out.println("Initialized the AuctionService");
    }

    private void demoTransactions() {
        // Create transactions for demonstration purposes
    }

    /*
     * args[0]: Address of one of the bootstrap nodes
     * args[1]: Port
     * args[2]: Consensus mechanism: PoW or PoS
     *
     * There should be > 1 bootstrap node to avoid a CPoF
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Invalid number of arguments\n\n");
            System.err.println("Usage:\nargs[0]: Address of a bootstrap node\nargs[1]: Port\nargs[2]: Consensus mechanism: PoW or PoS");
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

        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + args[1]);
        }


        switch (args[2]) {
            case "PoW" -> consensus = Consensus.PoW;
            case "PoS" -> consensus = Consensus.PoS;
            default -> throw new IllegalArgumentException("Invalid consensus mechanism: " + args[2]);
        }

        DHT dht = new DHT(bootstrapNodeAddress, consensus);
    }
}
