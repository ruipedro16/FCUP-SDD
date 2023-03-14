package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.p2p.Node;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcServer {
    private static final Logger logger = LogManager.getLogger(GrpcServer.class);

    // private ServerBuilder serverBuilder;
    private Server server;
    private GrpcServerServiceImpl protoServerService;

    public GrpcServer() {
        //empty constructor
    }

    public GrpcServer(int port) throws IOException {
        /*
        serverBuilder = ServerBuilder.forPort(port); // 80
        serverBuilder.addService(protoServerService); // protobuf
        server = serverBuilder.build();
         */
        server = ServerBuilder.forPort(port)
                .addService(protoServerService)
                .build();
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
