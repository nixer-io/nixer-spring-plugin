package io.nixer.nixerplugin.core.detection.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import io.nixer.nixerplugin.core.detection.rules.ratio.FailedLoginRatioRule;
import org.springframework.boot.convert.DurationUnit;

/**
 * Configures {@link FailedLoginRatioRule}.
 */
public class FailedLoginRatioProperties {

    /**
     * Whether failed login ratio feature is enabled.
     */
    private boolean enabled;

    /**
     * Value of `failed-login-ratio` metric above which an activation event will be generated.
     *
     * Unit of the metric is percent [%] and it is calculated with the following formula:
     *
     * `failed-login-ratio = (100 * number or failed logins) / (number of all logins)`.
     *
     * The activation level together with the deactivation level create hysteresis to better cope with credential stuffing
     * and also to prevent too frequent activation/deactivation events.
     */
    private int activationLevel = 80;

    /**
     * Value of `failed-login-ratio` metric below which a deactivation event will be generated.
     *
     * Unit of the metric is percent [%] and it is calculated with the following formula:
     *
     * `failed-login-ratio = (100 * number or failed logins) / (number of all logins)`.
     *
     * The activation level together with the deactivation level create hysteresis to better cope with credential stuffing
     * and also to prevent too frequent activation/deactivation events.
     */
    private int deactivationLevel = 70;

    /**
     * Property minimumSampleSize defines the smallest number of login attempts that need to occur within window for the activation to happen.
     * The reason for this property is that when there is a small number of login attempts, we don’t necessarily want to trigger activation.
     */
    private int minimumSampleSize = 20;

    /**
     * Property window defines time period for which the ratio will be calculated. Longer periods would consume more memory
     * (unless external data store is used) and would cause slower reaction to changes in traffic patterns.
     */
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
