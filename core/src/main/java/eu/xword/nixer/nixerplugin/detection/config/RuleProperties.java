package eu.xword.nixer.nixerplugin.detection.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.convert.DurationUnit;

public class RuleProperties {

    /**
     * Whether rule is enabled. Disabled by default.
     */
    private boolean enabled;

    /**
     * Window size in minutes that will be used to calculate metric.
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration window;

    /**
     * Defines at what metric value rule will trigger
     */
    private Integer threshold;

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(final Integer threshold) {
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
