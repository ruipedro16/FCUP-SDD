package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.ssd.p2p.Node;

import java.io.IOException;

public class GrpcServer {
    // add server
    // add server builder
    private ServerBuilder serverBuilder;
    private Server server;

    public GrpcServer() {
        //empty constructor
    }

    public GrpcServer(int port) throws IOException {
        serverBuilder = ServerBuilder.forPort(port); // 80
        // serverBuilder.addService(null); // Add todo: Add service (from protobuf)
        server = serverBuilder.build();
        server.start();
    }

    // init
    public Node init(byte[] id, int port) {
        Node node = new Node(id, port);
        System.out.println("Test");
        return node;
    }
}
