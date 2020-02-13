package io.nixer.nixerplugin.core.detection.events;

import java.util.Objects;

/**
 * Needed in registry to capture both activation and deactivation events
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
        return Objects.hash(this.getTimestamp(), this.getSource());
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
