package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.ssd.p2p.Node;

import java.io.IOException;

public class GrpcServer {
    private ServerBuilder serverBuilder;
    private Server server;
    private GrpcServerServiceImpl protoServerService;

    public GrpcServer() {
        //empty constructor
    }

    public GrpcServer(int port) throws IOException {
        serverBuilder = ServerBuilder.forPort(port); // 80
        serverBuilder.addService(protoServerService); // protobuf
        server = serverBuilder.build();
        server.start();
    }

    /**
     * Initializes the node
     * @param id node id
     * @param port node port
     * @return node
     */
    public Node init(byte[] id, int port) {
        Node node = new Node(id, port);
        System.out.println("Test");
        return node;
    }

    /**
     * Auto init that generates a random node id if none is provided
     * @param port node port
     * @return node
     */
    public Node autoInit(int port) {
        Node node = new Node(port);
        System.out.println("Test");
        return node;
    }
}
