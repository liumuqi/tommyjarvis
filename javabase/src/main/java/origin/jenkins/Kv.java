package origin.jenkins;

/**
 * @author muqi.lmq
 * @date 2018/7/11.
 */
public class Kv<K, V> {
    private K k;
    private V v;

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }
}
