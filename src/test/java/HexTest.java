import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class HexTest {
    @Test
    public void testToHexString() {
        // Test an empty byte array
        assertEquals("", Hex.toHexString(new byte[]{}));

        // Test a byte array with a single byte
        assertEquals("01", Hex.toHexString(new byte[]{1}));

        // Test a byte array with multiple bytes
        assertEquals("48656c6c6f20576f726c64", Hex.toHexString("Hello World".getBytes()));
    }

    @Test
    public void testDecode() {
        // Test an empty hex string
        assertArrayEquals(new byte[]{}, Hex.decode(""));

        // Test a hex string with a single byte
        assertArrayEquals(new byte[]{1}, Hex.decode("01"));

        // Test a hex string with multiple bytes
        assertArrayEquals("Hello World".getBytes(), Hex.decode("48656c6c6f20576f726c64"));

        // Test a hex string with odd number of characters
        assertThrows(DecoderException.class, () -> Hex.decode("0a1b2c3"));
    }
}
