import org.junit.Test;
import org.ssd.p2p.Node;

import static org.junit.Assert.assertEquals;

public class NodeTest {
    @Test
    public void testEmptyArray() {
        byte[] seq = new byte[0];
        int prefix = Node.getPrefixLength(seq);
        assertEquals(0, prefix);
    }

    @Test
    public void testPrefixAllZeros() {
        byte[] seq = {(byte) 0x00, (byte) 0x00, (byte) 0x00};
        int prefix = Node.getPrefixLength(seq);
        assertEquals(24, prefix);
    }

    @Test
    public void testPrefixAllOnes() {
        byte[] seq = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        int prefix = Node.getPrefixLength(seq);
        assertEquals(0, prefix);
    }

    @Test
    public void testPrefixMixed() {
        byte[] seq = {(byte) 0x00, (byte) 0xFF, (byte) 0x7F};
        int prefix = Node.getPrefixLength(seq);
        assertEquals(8, prefix);
    }
}
