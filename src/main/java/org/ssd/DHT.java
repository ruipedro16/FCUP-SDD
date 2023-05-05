package org.ssd;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.AuctionService;
import org.ssd.constants.KademliaConstants;
import org.ssd.ledger.BlockchainManager;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.p2p.Node;
import org.ssd.p2p.communication.CommunicationManager;
import org.ssd.p2p.grpc.KadServer;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.Utils;

import java.net.InetAddress;

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

    private static void initNetwork(boolean isBootstrap, int port) {
        if (isBootstrap) {
            DHT.node = new Node(KademliaConstants.BOOTSTRAP_NODE_ID, KademliaConstants.BOOTSTRAP_NODE_PORT);
        } else {
            DHT.node = new Node(null, port);

            InetAddress localhost = Utils.getLocalHostAddress();
            NodeContact bootstrapContact = new NodeContact(
                    localhost, KademliaConstants.BOOTSTRAP_NODE_PORT,
                    KademliaConstants.BOOTSTRAP_NODE_ID, System.currentTimeMillis());

            new Thread(() -> {
                DHT.node.joinNetwork(bootstrapContact);
            }).start();
        }
        System.out.println("Initialized the Kademlia Network");
    }

    private static void initBlockchain(@NonNull Consensus consensus) {
        DHT.blockchain = new Blockchain();
        System.out.println("Initialized the blockchain");

        if (consensus.equals(Consensus.PoW)) {
            DHT.blockchainManager = new MiningManager(DHT.blockchain);
            System.out.println("Initialized the mining manager");
        } else {
            DHT.blockchainManager = new StakingManager(DHT.blockchain);
            System.out.println("Initialized the staking manager");
        }
    }

    private static void initAuctionService() {
        DHT.auctionsService = new AuctionService();
        System.out.println("Initialized the Auction Service");
    }

    private static void initCommunicationManager() {
        DHT.communicationManager = new CommunicationManager(DHT.node, DHT.blockchain, DHT.auctionsService);
        // DHT.node.getServer().registerMessageSubscriber();
        // TODO: doesnt compile

        System.out.println("Initialized the Communication Manager");
    }

    private static void start(int port, @NonNull Consensus consensus, boolean isBootstrap) {
        initNetwork(isBootstrap, port);
        initBlockchain(consensus);
        initAuctionService();
        initCommunicationManager();
    }

    public static void main(String[] args) throws Exception {
        /*
         * args[0]: PORT
         * args[1]: PoW / PoS
         * args[2]: bootstrap [Optional: default is false]
         */
        int port = 0;
        Consensus consensus = null;
        boolean isBootstrap = false;

        // Check that the correct number of command line arguments were provided
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: <port number> <consensus> [bootstrap]");
            System.exit(1);
        }

        // Parse the port number from the first command line argument
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            System.exit(1);
        }

        // Parse the algorithm from the second command line argument
        if (args[1].equalsIgnoreCase("PoW")) {
            consensus = Consensus.PoW;
        } else if (args[1].equalsIgnoreCase("PoS")) {
            consensus = Consensus.PoS;
        } else {
            System.err.println("Invalid consensus: " + args[1]);
            System.exit(1);
        }

        // Check if the optional third command line argument was provided and set the isBootstrap flag accordingly
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("bootstrap")) {
                isBootstrap = true;
            } else if (!args[2].equalsIgnoreCase("regular")) {
                System.err.println("Invalid bootstrap type: " + args[2]);
                System.exit(1);
            }
        }

        start(port, consensus, isBootstrap);
    }
}
