package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.util.Assert;

/**
 * Sliding window counter implementation. Tracks individually every occurrence per key.
 * Internally creates independent counter for each key. Housekeeping happens on sum() call.
 *
 */
@ThreadSafe
public class RollingCounter {

    //todo consider two problems
    // 1. cleanup of expired counters
    // 2. O(n) for sum operation and memory.
    private final ConcurrentHashMap<String, Count> counts = new ConcurrentHashMap<>();

    private Clock clock = Clock.systemDefaultZone();

    private final long window;

    public RollingCounter(final Duration window) {
        Assert.notNull(window, "Window must not be null");
        // todo check if window length makes sense
        this.window = window.toMillis();
    }

    public void add(String key) {
        Assert.notNull(key, "Key must not be null");
        final long millis = clock.millis();
        counts.compute(key, (k, count) -> {
            if (count == null) {
                count = new Count();
            }
            count.add(millis);
            return count;
        });
    }

    public void remove(String key) {
        Assert.notNull(key, "Key must not be null");

        counts.remove(key);
    }

    public int get(String key) {
        Assert.notNull(key, "Key must not be null");

        final Count count = counts.get(key);
        if (count != null) {
            final int sum = count.sum();
            if (sum == 0) {
                counts.remove(key);
            }
            return sum;
        } else {
            return 0;
        }
    }

    @VisibleForTesting
    public int keysSize() {
        return counts.size();
    }

    private class Count {

        //todo use int instead of long
        //todo consider using fastutil IntList
        private final LinkedList<Long> counts = new LinkedList<>();

        synchronized void add(long timestamp) {
            counts.addLast(timestamp);
        }

        synchronized int sum() {
            final long now = RollingCounter.this.clock.millis();
            if (counts.isEmpty()) {
                return 0;
            }
            long expireOlderThan = now - RollingCounter.this.window;
            if (expireOlderThan > counts.getLast()) {
                return 0; // expired
            } else {
                for (int i = 0; i < counts.size(); i++) {
                    long time = counts.get(i);
                    if (time < expireOlderThan) {
                        counts.removeFirst();
                    } else {
                        break;
                    }
                }
                return counts.size();
            }
        }
    }

    public void setClock(final Clock clock) {
        Assert.notNull(clock, "Clock must not be null");

        this.clock = clock;
    }
}
