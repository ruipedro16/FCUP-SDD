import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.ssd.p2p.Node;

import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class NodeTest {
    @Test
    public void testXorDistance() {
        byte[] a = {0x01, 0x02, 0x03, 0x04};
        byte[] b = {0x05, 0x06, 0x07, 0x08};
        byte[] c = {0x04, 0x04, 0x04, 0x0C};

        BigInteger expected = new BigInteger(1, c);
        BigInteger actual = Node.getDistance(a, b);

        assertEquals(expected, actual);
    }

    @Test
    public void testGenId() throws UnknownHostException {
        Node node1 = new Node(null, 5005);
        System.out.println("GenID = " + Hex.toHexString(node1.getCurrentNode().getId()));
        assertEquals(node1.getCurrentNode().getId().length, 20); //20bytes
    }

}
