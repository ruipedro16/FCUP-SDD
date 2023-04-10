package org.ssd.p2p.storage;

import lombok.Data;

import java.util.*;

@Data
public class Storage {
    //todo custom methods to save, put, etc

    /**
     * key-List<values>. keeps a list of values per key
     * The key is formed by the data owner node ID and the key for the value to store
     */
    private final Map<byte[], StorageWithHeaderData> store;

    public Storage() {
        store = new HashMap<>();
    }

    /*
     * returns the list of byte arrays associated with the given key in the map.
     */
    public StorageWithHeaderData getValue(byte[] key) {
        return this.getStore().get(key);
    }

    /*
     * returns a boolean indicating whether the given key is present in the map.
     */
    public boolean hasKey(byte[] key) {
        return this.getStore().containsKey(key);
    }

    /*
     * adds the given value to the associated with the given key in the map.
     * If the key does not already exist in the map, a new entry is created
     */
    public void addValueToKey(byte[] key, StorageWithHeaderData value) {
        if (this.hasKey(key)) {
            if (this.getValue(key).getUpdateTime() <= value.getUpdateTime()) {
                this.getStore().replace(key, value);
            }
            //else if time is larger locally, then we have the updated value
        } else {
            Objects.requireNonNull(this.getStore().put(key, value));
        }
    }

}
