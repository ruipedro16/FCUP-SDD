import org.junit.Test;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;

public class KadLookupTest {

    @Test
    public void testNetworkAndLookUp() {
        //init network
        Node node1 = new Node(KademliaConstants.BOOTSTRAP_NODE_ID, KademliaConstants.BOOTSTRAP_NODE_PORT);
        Node node2 = new Node(null, 9000);
        Node node3 = new Node(null, 9001);
        Node node4 = new Node(null, 9002);
        Node node5 = new Node(null, 9003);
        Node node6 = new Node(null, 9004);
        Node node7 = new Node(null, 9005);
        Node node8 = new Node(null, 9006);
        Node node9 = new Node(null, 9007);
        Node node10 = new Node(null, 9008);

        //join network
        node2.joinNetwork(node1.getCurrentNode());
        node3.joinNetwork(node1.getCurrentNode());
        node4.joinNetwork(node2.getCurrentNode());
        node5.joinNetwork(node2.getCurrentNode());
        node6.joinNetwork(node5.getCurrentNode());
        node7.joinNetwork(node3.getCurrentNode());
        node8.joinNetwork(node6.getCurrentNode());
        node9.joinNetwork(node7.getCurrentNode());
        node10.joinNetwork(node1.getCurrentNode());
        //TODO: Routing table toString to show the buckets better
        System.out.println("[Node10] RoutingTable: " + node10.getRoutingTable().toString());

    }

}
