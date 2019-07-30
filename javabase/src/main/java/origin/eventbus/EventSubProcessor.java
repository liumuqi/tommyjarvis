package origin.eventbus;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.Executors;

/**
 * @author muqi.lmq
 * @date 22/12/2016.
 */
public class EventSubProcessor {
    private static AsyncEventBus eventBus = new AsyncEventBus(Executors.newWorkStealingPool(10));

    private EventSubProcessor() {
    }

    public static void register(Object o) {
        eventBus.register(o);
    }

    public static void post(Object o) {
        eventBus.post(o);
    }
}
