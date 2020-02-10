package io.nixer.nixerplugin.core.detection.events;

import java.util.Objects;

public class FailedLoginRatioActivationEvent extends FailedLoginRatioEvent {

    public FailedLoginRatioActivationEvent(double ratio) {
        super(ratio);
    }

    @Override
    public String type() {
        return FAILED_LOGIN_RATIO_ACTIVATION;
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
        final FailedLoginRatioActivationEvent that = (FailedLoginRatioActivationEvent) o;
        return this.getTimestamp() == (that.getTimestamp()) &&
                this.getSource().equals(that.getSource());
    }
}
