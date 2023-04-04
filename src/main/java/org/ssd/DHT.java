package org.ssd;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.auction.AuctionsService;
import org.ssd.ledger.Wallet;
import org.ssd.ledger.block.Blockchain;
import org.ssd.ledger.Consensus;
import org.ssd.p2p.Node;
import org.ssd.p2p.grpc.GrpcServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;

@NoArgsConstructor
public class DHT {
    private static final Logger logger = LogManager.getLogger(DHT.class);

    //default port, use 80 for now
    public static final int PORT = 80;

    //node
    private Node node;
    //server
    private GrpcServer server;

    @Getter
    private static Blockchain blockchain;
    //auctions
    private AuctionsService auctionsService;
    private InetAddress bootstrapNodeAddress;

    @Getter
    private Wallet wallet;

    public DHT(InetAddress bootstrapNodeAddress, Consensus consensus) throws IOException {
        /*
        switch (consensus) {
            case PoW -> this.blockchain = new PoWBlockchain();
            case PoS -> this.blockchain = new PoSBlockchain();
        }
        */

        this.server = new GrpcServer();
        //if node id is provided at startup then use a different init
        this.node = this.server.autoInit(PORT); //defaults

        this.auctionsService = new AuctionsService(this.node);
        //add option for bootstrap
        this.bootstrapNodeAddress = bootstrapNodeAddress;


        // Blockchain
        this.wallet = new Wallet();
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