package org.ssd.p2p.storage;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class StorageWithHeaderData {

    private final byte[] dataOwnerId;
    private final byte[] value;
    private final long updateTime;

    public StorageWithHeaderData(byte[] ownerId, byte[] value) {
        this.updateTime = System.currentTimeMillis();
        this.dataOwnerId = ownerId;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int res = (Arrays.hashCode(dataOwnerId) ^ (Arrays.hashCode(dataOwnerId) >>> 3));
        res = 23 * res + Objects.hashCode(this.dataOwnerId);
        res = 23 * res + Objects.hashCode(this.value);
        return res;
    }

    @Override
    public boolean equals(Object obj) { //equals should ignore last update time
        if (obj instanceof StorageWithHeaderData) {
            return obj.hashCode() == this.hashCode();
        }
        return false;
    }

}
