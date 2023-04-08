package org.ssd.p2p;

import lombok.Data;
import org.ssd.utils.Pair;

import java.util.*;

@Data
public class Storage {
    //todo custom methods to save, put, etc

    /**
     * key-List<values>. keeps a list of values per key, last value of the list is the newest and most recently updated
     * The key is formed by the data owner node ID and the key for the value to store
     */
    private final Map<Pair<byte[], byte[]>, List<byte[]>> store;

    public Storage() {
        store = new HashMap<>();
    }

    /*
     * returns the list of byte arrays associated with the given key in the map.
     */
    public List<byte[]> getValues(Pair<byte[], byte[]> key) {
        return this.getStore().get(key);
    }

    /*
     * returns a boolean indicating whether the given key is present in the map.
     */
    public boolean hasKey(Pair<byte[], byte[]> key) {
        return this.getStore().containsKey(key);
    }

    /*
     * adds the given byte array value to the list of values associated with the given key in the map.
     * If the key does not already exist in the map, a new entry is created with an empty list of values before adding the new value.
     */
    public void addValueToKey(Pair<byte[], byte[]> key, byte[] value) {
        if (this.hasKey(key)) {
            this.getValues(key).add(value);
        } else {
            Objects.requireNonNull(this.getStore().put(key, new ArrayList<>())).add(value);
        }
    }

}
