package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssd.p2p.Node;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.concurrent.TimeUnit;

public class GrpcServer {
    private static final Logger logger = LogManager.getLogger(GrpcServer.class);
    private Server server;
    private GrpcServerServiceImpl protoServerService;
    private final GrpcStubRouter stubRouter;
    private final GrpcKadStubManager kadStubRouter;

    public GrpcServer() {
        //empty constructor
        this.stubRouter = GrpcStubRouter.getInstance();
        this.kadStubRouter = GrpcKadStubManager.getInstance();
    }

    /**
     * Initializes the node
     * @param id node id
     * @param port node port
     * @return node
     */
    /*
    public Node init(byte[] id, int port) throws IOException {
        Node node = new Node(id, port, stubRouter, kadStubRouter);
        server = ServerBuilder.forPort(port)
                .addService(protoServerService)
                .build();
        server.start();

        logger.info("gRPC server running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");

            try {
                if (server != null) {
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("gRPC server shut down");
        }));

        System.out.println("Test");
        return node;
    }

     */

    /**
     * Auto init that generates a random node id if none is provided
     * @param port node port
     * @return node
     */
    public Node autoInit(int port) throws IOException {
        Node node = new Node(port, stubRouter, kadStubRouter);

        server = ServerBuilder.forPort(port)
                .addService(protoServerService)
                .build();
        server.start();

        logger.info("gRPC server running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");

            try {
                if (server != null) {
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("gRPC server shut down");
        }));

        System.out.println("Test");
        return node;
    }
}
