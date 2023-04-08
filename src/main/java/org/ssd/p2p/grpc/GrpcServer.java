package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.ssd.p2p.Node;

import java.io.IOException;

public class GrpcServer {
    private Server server;
    private GrpcServerServiceImpl protoServerService;
    private final GrpcStubRouter stubRouter;
    private final GrpcKadStubManager kadStubRouter;

    public GrpcServer() {
        this.stubRouter = GrpcStubRouter.getInstance();
        this.kadStubRouter = GrpcKadStubManager.getInstance();
    }

    public Node autoInit(int port) throws IOException {
        Node node = new Node(port, stubRouter, kadStubRouter);
        server = ServerBuilder.forPort(port)
                .addService(new GrpcServerServiceImpl(node))
                .build();
        server.start();
        System.out.println("gRPC server running on port " + port);
        return node;
    }
}
