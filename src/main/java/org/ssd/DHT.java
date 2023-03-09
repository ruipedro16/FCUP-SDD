package org.ssd;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.ledger.Blockchain;
import org.ssd.ledger.Consensus;
import org.ssd.p2p.Node;
import org.ssd.p2p.grpc.GrpcServer;
import org.ssd.utils.Config;

@NoArgsConstructor
public class DHT {
    private static final Logger logger = LogManager.getLogger(DHT.class);

    //default port, use 80 for now
    public static final int PORT = 80;

    //node
    private Node node;
    //server
    private GrpcServer server;

    public DHT(String[] args) {
        this.server = new GrpcServer();
        //if node id is provided at startup then use a different init
        this.node = this.server.autoInit(PORT); //defaults
        //add option for bootstrap
    }

    //chain
    Blockchain blockchain = new Blockchain();//move this to its handler
    //auctions

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

        switch (args[1]) {
            case "PoW" -> Config.setConsensus(Consensus.PoW);
            case "PoS" -> Config.setConsensus(Consensus.PoS);
            default -> throw new IllegalArgumentException();
        }

        DHT dht = new DHT(); // todo
    }
}