package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.NonNull;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class KadServer implements AutoCloseable {
    private Server server;
    private GrpcServerServiceImpl grpcServerService;

    public void start(@NonNull Node currentNode, int port) throws IOException {
        this.grpcServerService = new GrpcServerServiceImpl(currentNode);

        this.server = ServerBuilder.forPort(port)
                .addService(this.grpcServerService)
                .build();
        this.server.start();
        System.out.println("gRPC server listening on port " + port + " ...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server...");
            try {
                if (this.server != null) {
                    this.server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public void awaitTermination() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }

    public void registerMessageSubscriber(@NonNull BiConsumer<NodeContact, byte[]> consumer) {
        this.grpcServerService.registerMessageSubscriber(consumer);
    }

    @Override
    public void close() throws Exception {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
        }
    }
}
