package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.ssd.p2p.Node;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcServer {
    private Server server;
    private GrpcServerServiceImpl protoServerService;
    private final GrpcStubRouter stubRouter;
    private final GrpcKadStubManager kadStubRouter;

    public GrpcServer() {
        this.stubRouter = GrpcStubRouter.getInstance();
        this.kadStubRouter = GrpcKadStubManager.getInstance();
    }

    public Node initBootstrapNode(byte[] id, int port) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        /*
        Node node = new Node(id, port, stubRouter, kadStubRouter);
        this.server = ServerBuilder.forPort(port)
                .addService(new GrpcServerServiceImpl(node))
                .build();
        this.server.start();
        System.out.println("gRPC server running on port " + port);
        return node;
         */
        return null;
    }

    public Node initRegularNode(int port) throws IOException {
        /*
        Node node = new Node(port, stubRouter, kadStubRouter);
        this.server = ServerBuilder.forPort(port)
                .addService(new GrpcServerServiceImpl(node))
                .build();
        this.server.start();
        System.out.println("gRPC server running on port " + port);
        return node;
         */
        return null;
    }

    public void shutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
        }
    }
}
