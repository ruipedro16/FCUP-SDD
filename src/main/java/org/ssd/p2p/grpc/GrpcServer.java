package org.ssd.p2p.grpc;

import org.ssd.p2p.Node;

public class GrpcServer {
    // add server
    // add server builder

    public GrpcServer() {
        //empty constructor
    }

    // init
    public Node init(byte[] id, int port) {
        Node node = new Node(id, port);
        System.out.println("Test");
        return node;
    }
}
