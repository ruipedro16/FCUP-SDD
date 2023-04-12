package org.ssd;

import lombok.Getter;
import lombok.NonNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ssd.auction.AuctionsService;
import org.ssd.constants.KademliaConstants;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.p2p.Node;
import org.ssd.p2p.NodeContact;
import org.ssd.p2p.grpc.GrpcServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.concurrent.TimeUnit;

public class DHT {
    //default port, use 80 for now
    public static final int PORT = 8085; // TODO: Remove this: port is set in args[1]

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

    public DHT(@NonNull NodeContact bootstrapContact, @NonNull Consensus consensus) throws IOException {
        DHT.server = new GrpcServer();
        // DHT.node = DHT.server.autoInit(PORT); // the node should be inserted in the network here
        initBlockchain(consensus);
        initAuctionService();
        DHT.wallet = new Wallet();
        //demoTransactions();
    }

    private void initBlockchain(@NonNull Consensus consensus) {
        DHT.blockchain = new Blockchain();
        System.out.println("Initialized the Blockchain");

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
        Wallet WALLET = new Wallet();
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();

        // the first transaction does not have inputs
        Transaction transaction = new Transaction(WALLET.getPublicKey(), wallet1.getPublicKey(), 10, null);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * args[0]: Address of one of the bootstrap nodes
     * args[1]: Port
     * args[2]: Consensus mechanism: PoW or PoS
     *
     * There should be > 1 bootstrap node to avoid a CPoF
     */
    public static void main(String[] args) throws Exception {
        InetAddress localIpAddress = null;
        try {
            localIpAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        assert localIpAddress != null;

        NodeContact bootstrapNodeContact = new NodeContact(KademliaConstants.DEFAULT_NODE_ID, localIpAddress, KademliaConstants.DEFAULT_PORT);
        DHT dht = new DHT(bootstrapNodeContact, Consensus.PoW);
        /*
        if (args.length != 3) {
            System.err.println("Invalid number of arguments\n\n");
            System.err.println("Usage [Regular Node]: args[0]: Address of a bootstrap node \t args[1]: Port \t args[2]: Consensus mechanism: PoW or PoS");
            System.exit(1);
        }

        InetAddress bootstrapNodeAddress;
        Consensus consensus;
        int port;

        try {
            bootstrapNodeAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + args[0], e);
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + args[1]);
        }

        // NodeContact bootstrapNodeContact = new NodeContact(Utils.toByteArray(args[1]), bootstrapNodeAddress, port);

        switch (args[2]) {
            case "PoW" -> consensus = Consensus.PoW;
            case "PoS" -> consensus = Consensus.PoS;
            default -> throw new IllegalArgumentException("Invalid consensus mechanism: " + args[2]);
        }

        InetAddress localIpAddress = null;
        try {
            localIpAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        NodeContact bootstrapNodeContact = new NodeContact(KademliaConstants.DEFAULT_NODE_ID, localIpAddress, KademliaConstants.DEFAULT_PORT);
        DHT dht = new DHT(bootstrapNodeContact, consensus);
         */
    }
}
