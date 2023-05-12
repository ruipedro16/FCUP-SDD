package org.ssd;

import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.*;
import org.ssd.constants.KademliaConstants;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.p2p.Node;
import org.ssd.p2p.communication.BlockMessage;
import org.ssd.p2p.communication.CommunicationManager;
import org.ssd.p2p.communication.MessageContent;
import org.ssd.p2p.grpc.KadServer;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DHT {
    private static Node node;

    @Getter
    private static KadServer server;

    @Getter
    private static AuctionService auctionsService;

    @Getter
    private static Blockchain blockchain;

    @Getter
    private static MiningManager miningManager; // NULL in PoS

    @Getter
    private static StakingManager stakingManager; // NULL in PoW

    @Getter
    private static Wallet wallet; // Public Key is here

    @Getter
    private static Consensus consensus;

    @Getter
    private static CommunicationManager communicationManager;

    private static void initNetwork(boolean isBootstrap, int port) throws UnknownHostException {
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

        DHT.wallet = new Wallet();
        System.out.println("Initialized the wallet");

        if (consensus.equals(Consensus.PoW)) {
            DHT.stakingManager = null;
            DHT.miningManager = new MiningManager(DHT.blockchain);
            System.out.println("Initialized the mining manager");
        } else {
            DHT.stakingManager = new StakingManager(DHT.blockchain);
            DHT.stakingManager.registerValidator(DHT.wallet.getPublicKey());
            System.out.println("Initialized the staking manager");
        }
    }

    private static void initAuctionService() {
        DHT.auctionsService = new AuctionService();
        System.out.println("Initialized the Auction Service");
    }

    private static void initCommunicationManager() {
        DHT.communicationManager = new CommunicationManager(DHT.node, DHT.blockchain, DHT.auctionsService);
        DHT.node.getServer().registerMessageSubscriber((sender, msg) -> {
            try {
                communicationManager.messageReceiver(sender, msg);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        if (DHT.consensus.equals(Consensus.PoW)) {
            DHT.miningManager.registerBlockConsumer(block -> {
                MessageContent msg = new BlockMessage(block);
                communicationManager.broadcastMessage(msg);
            });
        } else {
            DHT.stakingManager.registerBlockConsumer(block -> {
                MessageContent msg = new BlockMessage(block);
                communicationManager.broadcastMessage(msg);
            });
        }

        System.out.println("Initialized the Communication Manager");
    }

    private static void initMenu(boolean isBootstrap) {
        if (!isBootstrap) {
            //TODO: add 1 or 2 example auctions or even delete since it wont be advertised
            Auction example1 = new Auction(
                    new Item("TestItem",
                            wallet.getPublicKey(),
                            10
                    ),
                    wallet.getPublicKey());
            auctionsService.addAuction(new ActiveAuction(example1));
            auctionsService.publishAuction(example1);
        }

        Runnable actions = new AuctionUI();
        actions.run();
    }

    public static void start(int port, @NonNull Consensus consensus, boolean isBootstrap) throws UnknownHostException {
        initNetwork(isBootstrap, port);
        initBlockchain(consensus);
        initAuctionService();
        initCommunicationManager();
        initMenu(isBootstrap);
    }
}
