package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Objects;

public class PairPojo<K, V> implements Serializable {

    private K key;

    public K getKey() {
        return key;
    }

    private V value;

    public V getValue() {
        return value;
    }

    public PairPojo(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PairPojo) {
            PairPojo pairPojo = (PairPojo) o;
            if (!Objects.equals(key, pairPojo.key)) {
                return false;
            }
            return Objects.equals(value, pairPojo.value);
        }
        return false;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }
}