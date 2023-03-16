package org.ssd.p2p.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import org.ssd.p2p.Node;
import org.ssd.utils.ChannelUtils;
import org.ssd.utils.Triple;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Class to initialize and route ManagedChannels from node id's present and managed by the RoutingTable
 */
public class GrpcStubRouter {

    private static GrpcStubRouter instance;
    private HashMap<ByteString, ManagedChannel> channels;

    public GrpcStubRouter() {
        //init the hashmap
        instance = this;
    }

    public static GrpcStubRouter getInstance() {
        if (instance == null) {
            instance = new GrpcStubRouter();
        }
        return instance;
    }

    public ManagedChannel initChannel(Triple<byte[], InetAddress, Integer> target) {
        //verify if channel exists
        ManagedChannel channel = getChannel(target.getFirst());
            if (channel == null || channel.isTerminated() || channel.isShutdown()) {
                //init
                ManagedChannel created = ChannelUtils.initUnsecureChannel(target);//init ChannelUtils later on Main for provider
                //save connection to router's list
                saveChannel(target, created);
                return created;
            } else if (!channel.isShutdown() && !channel.isTerminated()) {
                return channel;
            }
            return null;
    }

    public void shutdownChannel(Triple<byte[], InetAddress, Integer> target) {
        ManagedChannel channel = getChannel(target.getFirst());
        if (channel.isShutdown() || channel.isTerminated()) {
            return;
        }
        channel.shutdown();
        this.channels.remove(ByteString.copyFrom(target.getFirst()));
    }

    /**
     * Function to search the stored channels for a nodeId
     * @param nodeId Node ID to search for
     * @return Returns a ManagedChannel based on the node id. Returns null if non is found.
     */
    public ManagedChannel getChannel(byte[] nodeId) {
        ByteString key = ByteString.copyFrom(nodeId);
        if (channels.containsKey(key)) {
            ManagedChannel channelValue = this.channels.get(key);
            if (channelValue.isTerminated() || channelValue.isShutdown()) {
                return null;//found but not available
            }
            return channelValue;//found
        }
        return null;// not found
    }

    public void saveChannel(Triple<byte[], InetAddress, Integer> target, ManagedChannel channel) {
        this.channels.put(ByteString.copyFrom(target.getFirst()), channel);
    }

    //replicate kademlia operations here
    public void ping(Triple<byte[], InetAddress, Integer> target, Node currentNode) {

    }

    public void store(Triple<byte[], InetAddress, Integer> target, Node currentNode, byte[] dataToStore) {//change data type

    }
    //findNode
    //findValue

}
