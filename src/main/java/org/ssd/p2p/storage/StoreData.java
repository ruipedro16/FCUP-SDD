package org.ssd.p2p.storage;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class StoreData {
    private final byte[] key;
    private byte[] value;

    private final byte[] originalPublisherID;

    private long lastRepublish;
    private long lastUpdate;

    public StoreData(byte[] key, byte[] value, byte[] originalPublisherID) {
        if (key == null || originalPublisherID == null) {
            throw new IllegalArgumentException();
        }

        this.key = key;
        this.value = value;
        this.originalPublisherID = originalPublisherID;
        this.lastRepublish = System.currentTimeMillis();
        this.lastUpdate = System.currentTimeMillis();
    }

    public void updateValue(byte[] value) {
        this.value = value;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void setLastRepublish() {
        this.lastRepublish = System.currentTimeMillis();
    }

    public void setLastUpdate() {
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StoreData that = (StoreData) o;
        return Arrays.equals(this.key, that.key);
    }
}
