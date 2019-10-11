package eu.xword.nixer.nixerplugin.detection.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.convert.DurationUnit;

public class RuleProperties {

    /**
     * Whether rule is enabled
     */
    private boolean enabled;

    /**
     * Window size in minutes that will be used to calculate metric.
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration window = WindowSize.WINDOW_5M;

    public static final int DEFAULT_THRESHOLD = 5;

    /**
     * Defines at what metric value rule will trigger
     */
    private int threshold = DEFAULT_THRESHOLD;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getWindow() {
        return window;
    }

    public void setWindow(final Duration window) {
        WindowSize.validate(window);

        this.window = window;
    }
}
