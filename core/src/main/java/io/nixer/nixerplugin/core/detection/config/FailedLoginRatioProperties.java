package io.nixer.nixerplugin.core.detection.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import io.nixer.nixerplugin.core.detection.rules.ratio.FailedLoginRatioRule;
import org.springframework.boot.convert.DurationUnit;

/**
 * Configures {@link FailedLoginRatioRule}.
 */
public class FailedLoginRatioProperties {

    private boolean enabled;

    private int activationLevel = 80;

    private int deactivationLevel = 70;

    private int minimumSampleSize = 20;

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration window = WindowSize.WINDOW_10M;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public int getActivationLevel() {
        return activationLevel;
    }

    public void setActivationLevel(final int activationLevel) {
        this.activationLevel = activationLevel;
    }

    public int getDeactivationLevel() {
        return deactivationLevel;
    }

    public void setDeactivationLevel(final int deactivationLevel) {
        this.deactivationLevel = deactivationLevel;
    }

    public int getMinimumSampleSize() {
        return minimumSampleSize;
    }

    public void setMinimumSampleSize(final int minimumSampleSize) {
        this.minimumSampleSize = minimumSampleSize;
    }

    public Duration getWindow() {
        return window;
    }

    public void setWindow(final Duration window) {
        this.window = window;
    }
}
