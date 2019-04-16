package origin.practise.rateLimit;

import java.util.LinkedList;

public class RateLimit_Window {
    private LinkedList<Integer> deque = new LinkedList<>();
    private int windowSize;
    private int windowIntervalMilliSeconds;
    private int currentWindowPermits;

    private long updateTimeStamp;
    private int intervalMilliSeconds;
    private int maxPermits;
    private long storedPermits;

    public RateLimit_Window(int maxPermits, int windowSize) {
        this(maxPermits, 1, windowSize);
    }

    public RateLimit_Window(int maxPermits, int intervalSeconds, int windowSize) {
        this.maxPermits = maxPermits;
        this.intervalMilliSeconds = intervalSeconds * 1000;
        this.windowSize = windowSize;
        this.windowIntervalMilliSeconds = intervalMilliSeconds / windowSize;
    }

    public synchronized Boolean acquire(int permits) {
        while (true) {
            long now = System.currentTimeMillis();
            if (now < updateTimeStamp + windowIntervalMilliSeconds) {
                if (storedPermits + permits + currentWindowPermits <= maxPermits) {
                    currentWindowPermits += permits;
                    updateTimeStamp = now;
                    return true;
                } else {
                    return false;
                }
            } else {
                updateTimeStamp = now;
                deque.offerLast(currentWindowPermits);
                storedPermits += currentWindowPermits;
                currentWindowPermits = 0;
                while (deque.size() > windowSize) {
                    storedPermits -= deque.removeFirst();
                }
            }
        }
    }
}
