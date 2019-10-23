package eu.xword.nixer.nixerplugin.core.detection.config;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * Predefined time windows for windowing.
 */
public class WindowSize {

    private static final Set<Duration> WINDOWS = new HashSet<>();

    public static final Duration WINDOW_1M = Duration.ofMinutes(1);
    public static final Duration WINDOW_5M = Duration.ofMinutes(5);
    public static final Duration WINDOW_15M = Duration.ofMinutes(15);

    static {
        WINDOWS.add(WINDOW_1M);
        WINDOWS.add(WINDOW_5M);
        WINDOWS.add(WINDOW_15M);
    }

    public static void validate(final Duration window) {
        Assert.isTrue(WINDOWS.contains(window), () -> "Window must be one of " + WINDOWS);
    }


    private WindowSize() {
    }
}
