import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteStore;
import org.ssd.p2p.storage.StoreData;

import java.net.UnknownHostException;

public class KadStoreTest {

    @Test
    public void testStore() throws UnknownHostException {
        Node node1 = new Node(KademliaConstants.BOOTSTRAP_NODE_ID, KademliaConstants.BOOTSTRAP_NODE_PORT);
        Node node2 = new Node(null, 9000);
        Node node3 = new Node(null, 9001);
        Node node4 = new Node(null, 9002);
        Node node5 = new Node(null, 9003);

        node2.joinNetwork(node1.getCurrentNode());
        node3.joinNetwork(node1.getCurrentNode());
        node4.joinNetwork(node1.getCurrentNode());
        node5.joinNetwork(node1.getCurrentNode());

        System.out.println("{Node5} [" + Hex.toHexString(node5.getCurrentNode().getId()) + "] RoutingTable: \n" + node5.getRoutingTable().toStringOmitEmpty());
        StoreData dataToStore = new StoreData(node2.getCurrentNode().getId(), "data".getBytes(), node5.getCurrentNode().getId());
        System.out.println(node1.getDht().isEmpty());
        System.out.println(node2.getDht().isEmpty());
        System.out.println(node3.getDht().isEmpty());
        System.out.println(node4.getDht().isEmpty());

        new KadRemoteStore(node5, dataToStore).trigger();
        System.out.println(node1.getDht().isEmpty());
        System.out.println(node2.getDht().isEmpty());
        System.out.println(node3.getDht().isEmpty());
        System.out.println(node4.getDht().isEmpty());

        System.out.println(node1.getDht().toString());
        System.out.println(node2.getDht().toString());
        System.out.println(node3.getDht().toString());
        System.out.println(node4.getDht().toString());
    }

    /**
     * EX:
     * Bucket[0]
     * (0004e778a0a48b60d8840a7835b98e7c8c19ca4a, DESKTOP-JL1762N/172.18.80.1, 9008)
     *
     * Bucket[146]
     * (00028470dc86ef5cf9ad90179601cdf201922e04, /172.18.80.1, 9006)
     *
     * Bucket[147]
     * (000ee3d06ecab080f2bb833135bdb164d10923ee, /172.18.80.1, 9007)
     * (000a4ad339b1d55e9d4dd42ddce640b6f2803963, /172.18.80.1, 9005)
     *
     * Bucket[158]
     * (4405d03d7d92511556900e6cfacde2366e75a1e8, DESKTOP-JL1762N/172.18.80.1, 8080)
     *
     * KadDHT
     * : [
     * (000a4ad339b1d55e9d4dd42ddce640b6f2803963, 64617461)
     * ]
     * KadDHT
     * : [
     * (000a4ad339b1d55e9d4dd42ddce640b6f2803963, 64617461)
     * ]
     * KadDHT
     * : [
     * (000a4ad339b1d55e9d4dd42ddce640b6f2803963, 64617461)
     * ]
     * KadDHT
     * : [
     * (000a4ad339b1d55e9d4dd42ddce640b6f2803963, 64617461)
     * ]
     */

}
