package eu.xword.nixer.nixerplugin.login.inmemory;

import java.time.Clock;
import java.time.Duration;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
    private final LoadingCache<String, Count> counts;

    private Clock clock = Clock.systemDefaultZone();

    private final long window;

    public RollingCounter(final Duration window) {
        Assert.notNull(window, "Window must not be null");
        // todo check if window length makes sense
        this.window = window.toMillis();
        this.counts = CacheBuilder.newBuilder()
                .expireAfterAccess(window.plusMinutes(1))
                .build(new CacheLoader<String, Count>() {
                    @Override
                    public Count load(final String key) throws Exception {
                        return new Count();
                    }
                });
    }

    public void add(String key) {
        Assert.notNull(key, "Key must not be null");
        final long millis = clock.millis();
        final Count count;
        try {
            count = counts.get(key);
            count.add(millis);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        Assert.notNull(key, "Key must not be null");

        counts.invalidate(key);
    }

    public int get(String key) {
        Assert.notNull(key, "Key must not be null");

        final Count count = counts.getIfPresent(key);
        if (count != null) {
            final int sum = count.sum();
            if (sum == 0) {
                counts.invalidate(key);
            }
            return sum;
        } else {
            return 0;
        }
    }

    @VisibleForTesting
    public long keysSize() {
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
