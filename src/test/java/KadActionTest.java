import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.ssd.constants.KademliaConstants;
import org.ssd.p2p.Node;
import org.ssd.p2p.routing.NodeContact;

import java.net.InetAddress;

public class KadActionTest {
    @Test
    public void testBootstrap() throws Exception {
        Node node1 = new Node(KademliaConstants.BOOTSTRAP_NODE_ID, KademliaConstants.BOOTSTRAP_NODE_PORT);
        Node node2 = new Node(null, 5005); // the node ID will be generated

        System.out.println("Generated node ID: " + Hex.toHexString(node2.getCurrentNode().getId()));

        NodeContact node2Contact = new NodeContact(InetAddress.getLocalHost(), 5005, node2.getCurrentNode().getId(), System.currentTimeMillis());
        node1.joinNetwork(node2Contact);
    }
}
