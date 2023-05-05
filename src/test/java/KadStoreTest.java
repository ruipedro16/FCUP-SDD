import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.remote.KadRemoteStore;
import org.ssd.p2p.storage.StoreData;

import static org.junit.Assert.assertTrue;

public class KadStoreTest {

    @Test
    public void testStore() {
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
    }
}
