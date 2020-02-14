package io.nixer.nixerplugin.core.detection.events;

import java.util.Objects;

/**
 * Needed in registry to capture both activation and deactivation events
 *
 * Keeps ratio as an event source. Ratio is a double ranged [0 - 1] representing ratio of number of failed login attempts to number of all login
 * attempts.
 */
public abstract class FailedLoginRatioEvent extends AnomalyEvent {

    public static final String FAILED_LOGIN_RATIO_ACTIVATION = "FAILED_LOGIN_RATIO_ACTIVATION";
    public static final String FAILED_LOGIN_RATIO_DEACTIVATION = "FAILED_LOGIN_RATIO_DEACTIVATION";

    public FailedLoginRatioEvent(double ratio) {
        super(ratio);
    }

    public double getRatio() {
        return (double) getSource();
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTimestamp(), this.getRatio());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final FailedLoginRatioDeactivationEvent that = (FailedLoginRatioDeactivationEvent) o;
        return this.getTimestamp() == that.getTimestamp() &&
                this.getRatio() == that.getRatio();
    }

}
