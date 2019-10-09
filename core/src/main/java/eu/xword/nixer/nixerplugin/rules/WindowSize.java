package eu.xword.nixer.nixerplugin.rules;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Predefined time windows for windowing.
 */
public class WindowSize {

    private static final Map<String, Duration> WINDOW_BY_NAME = new HashMap<>();

    public static final Duration WINDOW_1M = Duration.ofMinutes(1);

    public static final Duration WINDOW_5M = Duration.ofMinutes(5);

    public static final Duration WINDOW_15M = Duration.ofMinutes(15);

    static {
        WINDOW_BY_NAME.put("1m", WINDOW_1M);
        WINDOW_BY_NAME.put("5m", WINDOW_5M);
        WINDOW_BY_NAME.put("15m", WINDOW_15M);
    }

    public static Duration fromString(final String window) {
        Assert.isTrue(WINDOW_BY_NAME.containsKey(window), () -> "Window must be one of " + WINDOW_BY_NAME.keySet());

        return WINDOW_BY_NAME.get(window);
    }

    private WindowSize() {
    }
}
