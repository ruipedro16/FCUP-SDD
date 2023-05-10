package org.ssd.p2p.storage;

import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KadDHT implements Map<byte[], StoreData> {
    private final Map<byte[], StoreData> storedDataMap;

    public KadDHT() {
        this.storedDataMap = new ConcurrentHashMap<>(); // thread-safe without the need for explicit synchronization
    }

    public void store(@NonNull StoreData data) {
        byte[] key = data.getKey();
        byte[] value = data.getValue();
        byte[] originalPublisherId = data.getOriginalPublisherID();

        this.store(key, value, originalPublisherId);
    }

    public void store(byte[] key, byte[] value, byte[] originalPublisherID) {
        if (key == null || originalPublisherID == null) {
            throw new IllegalArgumentException();
        }

        if (this.storedDataMap.containsKey(key)) { // if the key is in the map we update it
            StoreData valueData = this.storedDataMap.get(key);
            valueData.updateValue(value);
        } else { // otherwise we create a new StoreData object
            StoreData newData = new StoreData(key, value, originalPublisherID);
            this.storedDataMap.put(key, newData);
        }
    }

    @Override
    public int size() {
        return this.storedDataMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.storedDataMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.storedDataMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.storedDataMap.containsValue(value);
    }

    @Override
    public StoreData get(Object key) {
        return this.storedDataMap.get(key);

    }

    @Override
    public StoreData put(byte[] key, StoreData value) {
        return this.storedDataMap.put(key, value);
    }

    @Override
    public StoreData remove(Object key) {
        return this.storedDataMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends byte[], ? extends StoreData> m) {
        this.storedDataMap.putAll(m);
    }

    @Override
    public void clear() {
        this.storedDataMap.clear();
    }

    @Override
    public Set<byte[]> keySet() {
        return this.storedDataMap.keySet();
    }

    @Override
    public Collection<StoreData> values() {
        return this.storedDataMap.values();
    }

    @Override
    public Set<Entry<byte[], StoreData>> entrySet() {
        return this.storedDataMap.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KadDHT\n: [");
        this.storedDataMap.forEach((key, value) -> {
            sb.append("\n(");
            sb.append(Hex.toHexString(key));
            sb.append(", ");
            sb.append(Hex.toHexString(value.getValue()));
            sb.append(")");
        });
        sb.append("\n]");
        return sb.toString();
    }
}
