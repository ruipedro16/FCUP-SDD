package org.ssd;

import org.ssd.ledger.Blockchain;
import org.ssd.p2p.Node;
import org.ssd.p2p.grpc.GrpcServer;

import java.util.Random;

public class DHT {

    //default port, use 80 for now
    public static final int PORT = 80;

    //node
    private Node node;
    //server
    private GrpcServer server;

    public DHT(String[] args) {
        Random random = new Random();
        this.server = new GrpcServer();

        byte[] placeholder = new byte[160];
        random.nextBytes(placeholder);
        this.node = this.server.init(placeholder, PORT); //defaults
        //add option for bootstrap

    }
    //chain
    Blockchain blockchain = new Blockchain();//move this to its handler
    //auctions

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}