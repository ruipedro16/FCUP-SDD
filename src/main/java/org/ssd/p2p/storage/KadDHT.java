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
        // TODO: rever para ver se esta certo
        this.storedDataMap.computeIfPresent(key, (k, v) -> {
            v.updateValue(value);
            return v;
        });

        this.storedDataMap.computeIfAbsent(key, k -> new StoreData(key, value, originalPublisherID));
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
