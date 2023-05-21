package org.ssd;

import com.google.protobuf.ByteString;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.*;
import org.ssd.constants.KademliaConstants;
import org.ssd.ledger.Consensus;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.mining.MiningManager;
import org.ssd.ledger.staking.StakingManager;
import org.ssd.ledger.transactions.Transaction;
import org.ssd.ledger.transactions.TransactionOutput;
import org.ssd.p2p.Node;
import org.ssd.p2p.communication.*;
import org.ssd.p2p.grpc.KadServer;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.ssd.constants.BlockchainConstants.INITIAL_WALLET_BALANCE;

public class DHT {
    @Getter
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

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (consensus.equals(Consensus.PoW)) {
            DHT.consensus = Consensus.PoW;
            DHT.stakingManager = null;
            DHT.miningManager = new MiningManager(DHT.blockchain);
            System.out.println("Initialized the mining manager");
        } else {
            DHT.consensus = Consensus.PoS;
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
                Message msg = new BlockMessage(block);
                communicationManager.broadcastMessage(msg);
            });
        } else {
            DHT.stakingManager.registerBlockConsumer(block -> {
                Message msg = new BlockMessage(block);
                communicationManager.broadcastMessage(msg);
            });
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Initialized the Communication Manager");
    }

    private static void addFunds() {
        Wallet oneoff = new Wallet();
        Transaction fundT = new Transaction(oneoff.getPublicKey(), getWallet().getPublicKey(), INITIAL_WALLET_BALANCE, null);
        fundT.setSignature(oneoff.getPrivateKey());
        fundT.setId(Transaction.computeTransactionID(fundT));
        fundT.getTxOutputs().add(new TransactionOutput(fundT.getRecipient(), fundT.getAmount(), fundT.getId()));
        blockchain.getUTXOs().put(ByteString.copyFrom(fundT.getTxOutputs().get(0).getID()), fundT.getTxOutputs().get(0));
        blockchain.getTransactionPool().addTransaction(fundT);

        Message funds = new TransactionMessage(fundT);
        DHT.getCommunicationManager().broadcastMessage(funds);

    }

    private static void initMenu(boolean bootstrap) throws InterruptedException {
        InetAddress localhost = Utils.getLocalHostAddress();
        NodeContact bootstrapContact = new NodeContact(
                localhost, KademliaConstants.BOOTSTRAP_NODE_PORT,
                KademliaConstants.BOOTSTRAP_NODE_ID, System.currentTimeMillis());

        TimeUnit.SECONDS.sleep(3); //allow FIND_NODE to reply
        if (!bootstrap) {
            DHT.getCommunicationManager().sendMessage(new RequestBlockchainMessage(), bootstrapContact);
            TimeUnit.SECONDS.sleep(3);
            addFunds();
        } else {
            addFunds();
        }
        DHT.getAuctionsService().getNetworkAuctions(); // do this here, after all services are up
        Runnable actions = new AuctionUI();
        actions.run();
    }

    public static void testWallets(boolean test) {
        if (!test) return;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Wallet main = new Wallet();
        Wallet node1Wallet = new Wallet();
        Wallet node2Wallet = new Wallet();

        Transaction initT = new Transaction(main.getPublicKey(), node1Wallet.getPublicKey(), 100, null);
        initT.setSignature(main.getPrivateKey());
        initT.setId("0".getBytes(StandardCharsets.UTF_8));
        initT.getTxOutputs().add(new TransactionOutput(initT.getRecipient(), initT.getAmount(), initT.getId()));
        DHT.getBlockchain().getUTXOs().put(ByteString.copyFrom(initT.getTxOutputs().get(0).getID()), initT.getTxOutputs().get(0));
        DHT.getBlockchain().getTransactionPool().addTransaction(initT);

        System.out.println("node1Wallet's balance is: " + node1Wallet.getBalance());

        DHT.getBlockchain().getTransactionPool().addTransaction(node1Wallet.createTransaction(node2Wallet.getPublicKey(), 25f));
        System.out.println("node1Wallet's has: " + node1Wallet.getBalance());
        System.out.println("node2Wallet's has: " + node2Wallet.getBalance());

        System.out.println(DHT.getBlockchain().toString());
    }
    
    public static void start(int port, @NonNull Consensus consensus, boolean isBootstrap) throws UnknownHostException, InterruptedException {
        initNetwork(isBootstrap, port);
        initBlockchain(consensus);
        initAuctionService();
        initCommunicationManager();
        testWallets(false);
        initMenu(isBootstrap);
    }
}
