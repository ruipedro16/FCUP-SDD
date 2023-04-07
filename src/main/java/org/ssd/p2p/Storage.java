package org.ssd.p2p;

import lombok.Data;
import org.ssd.utils.Pair;

import java.util.*;

@Data
public class Storage {

    /**
     * key-List<values>. keeps a list of values per key, last value of the list is the newest and most recently updated
     * The key is formed by the data owner node ID and the key for the value to store
     */
    private final Map<Pair<byte[], byte[]>, List<byte[]>> store;

    public Storage() {
        store = new HashMap<>();
    }

    //todo custom methods to save, put, etc
    public List<byte[]> getValues(Pair<byte[], byte[]> key) {
        return this.getStore().get(key);
    }

    public boolean hasKey(Pair<byte[], byte[]> key) {
        return this.getStore().containsKey(key);
    }

    public void addValueToKey(Pair<byte[], byte[]> key, byte[] value) {
        if (this.hasKey(key)) {
            this.getValues(key).add(value);
        } else {
            Objects.requireNonNull(this.getStore().put(key, new ArrayList<>())).add(value);
        }
    }

}
