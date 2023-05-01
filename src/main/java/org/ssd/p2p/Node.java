package org.ssd.p2p;

import com.google.common.primitives.Longs;
import lombok.Getter;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.grpc.KadClientManager;
import org.ssd.p2p.grpc.KadServer;
import org.ssd.p2p.remote.KadRemoteFindNode;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.routing.RoutingTable;
import org.ssd.p2p.storage.KadDHT;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Node {
    private final NodeContact currentNode;
    private final KadServer server;
    private final KadClientManager clientManager;
    private final RoutingTable routingTable;
    private final KadDHT dht;
    /**
     * List of the IDs of the messages that this node has seen
     */
    private final List<byte[]> seenMessages;

    public Node(byte[] nodeID, int port) {
        if (nodeID == null) {
            System.out.println("Generating node ID...");
            nodeID = generateNodeID(port);
            System.out.println("Generated node ID: " + Hex.toHexString(nodeID));
        }

        this.currentNode = new NodeContact(Utils.getLocalHostAddress(), port, nodeID, System.currentTimeMillis());

        this.server = new KadServer();
        new Thread(() -> {
            try {
                this.server.start(this, port);
                this.server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        this.clientManager = new KadClientManager();
        this.routingTable = new RoutingTable(this.currentNode);
        this.dht = new KadDHT();
        this.seenMessages = new ArrayList<>();
    }

    /**
     * Calculates the XOR distance between two byte arrays of the same length.
     *
     * @param id1 the first byte array
     * @param id2 the second byte array
     * @return the XOR distance between id1 and id2
     * @throws IllegalArgumentException if either id1 or id2 is null, or if their length is not equal to KademliaConstants.B
     */
    public static BigInteger getDistance(byte[] id1, byte[] id2) {
        if (id1 == null || id2 == null || id1.length != id2.length) {
            throw new IllegalArgumentException();
        }

        BigInteger b1 = new BigInteger(1, id1);
        BigInteger b2 = new BigInteger(1, id2);

        return b1.xor(b2);
    }

    private byte[] generateNodeID(int port) {
        byte[] res = new byte[KademliaConstants.B / Byte.SIZE];
        new SecureRandom().nextBytes(res);
        long nonce = 0L;
        InetAddress currentAddress = Utils.getLocalHostAddress();

        String target = new String(new char[KademliaConstants.PREFIX_LENGTH]).replace('\0', '0');
        while (!Hex.toHexString(res).substring(0, KademliaConstants.PREFIX_LENGTH).equals(target)) {
            nonce++;
            byte[] dataToHash = Arrays.concatenate(
                    Longs.toByteArray(nonce),
                    currentAddress.getAddress(),
                    Utils.toByteArray(port)
            );
            res = CryptoUtils.hash(dataToHash);

        }
        return res;
    }

    /**
     * Join the network by adding the bootstrap node to the routing table and looking up itself
     *
     * @param nodeContact
     */
    public void joinNetwork(@NonNull NodeContact nodeContact) {
        this.routingTable.addContact(nodeContact);
        new KadRemoteFindNode(this, this.currentNode.getId()).trigger();

    }

    public boolean addToSeenMessages(byte[] messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("ID of the message cannot be null");
        }

        boolean messageExists = seenMessages.stream().anyMatch(m -> java.util.Arrays.equals(m, messageId));

        if (messageExists) {
            return false;
        } else {
            seenMessages.add(messageId);
            return true;
        }
    }
}
