package origin.concurrency.bugs;


import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 使用cas 做无锁链表
 * AtomicStampedReference
 * AtomicMarkableReference
 *
 * @param <T>
 */
public class TopNode<T> {
    AtomicStampedReference<TopNode<T>> top = new AtomicStampedReference<>(null, 0);
    private T value;
    private TopNode<T> next;

    public TopNode<T> pop() {
        TopNode<T> topNode;
        int stamp;
        while ((topNode = top.getReference()) != null
                && (stamp = top.getStamp()) != 0 && !top.compareAndSet(topNode, topNode.getNext(), stamp, stamp + 1)) ;
        if (topNode != null) {
            topNode.setNext(null); // help gc
        }
        return topNode;
    }

    public void push(TopNode<T> node) {
        TopNode<T> topNode;
        int stamp;
        do {
            topNode = top.getReference();
            stamp = top.getStamp();
            node.setNext(topNode);
        } while (!top.compareAndSet(topNode, node, stamp, stamp + 1));
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public TopNode<T> getNext() {
        return next;
    }

    public void setNext(TopNode<T> next) {
        this.next = next;
    }

    public AtomicStampedReference<TopNode<T>> getTop() {
        return top;
    }

    public void setTop(AtomicStampedReference<TopNode<T>> top) {
        this.top = top;
    }
}
