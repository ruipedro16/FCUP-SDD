package org.ssd.p2p.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.NonNull;
import org.ssd.p2p.Node;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KadServer implements AutoCloseable {
    private Server server;

    public void start(@NonNull Node currentNode, int port) throws IOException {
        this.server = ServerBuilder.forPort(port)
                .addService(new GrpcServerServiceImpl(currentNode))
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

    @Override
    public void close() throws Exception {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(15, TimeUnit.SECONDS);
        }
    }
}
