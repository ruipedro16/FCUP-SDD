package org.ssd.auction;

import lombok.Data;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;

@Data
public class Item implements Serializable {
    private final byte[] itemID;

    @Override
    public String toString() {
        return Hex.toHexString(this.itemID);
    }
}
