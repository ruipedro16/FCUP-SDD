package org.ssd.p2p.grpc;

import io.grpc.ManagedChannel;

import java.util.HashMap;

/**
 * Class to initialize and route ManagedChannels from node id's present and managed by the RoutingTable
 */
public class GrpcStubRouter {

    //ByteString instead of String
    private HashMap<String, ManagedChannel> channels;

    public GrpcStubRouter() {
        //init the hashmap
    }

    //create connections to closest/connected nodes based on RoutingTable
    //todo
}
