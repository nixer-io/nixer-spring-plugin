package io.nixer.nixerplugin.core.fingerprint.loginThreshold;

import com.google.common.base.Objects;
import io.nixer.nixerplugin.core.detection.events.AnomalyEvent;
import io.nixer.nixerplugin.core.detection.events.EventVisitor;

public class FingerprintFailedLoginOverThresholdEvent extends AnomalyEvent {

    public FingerprintFailedLoginOverThresholdEvent(final String fingerprint) {
        super(fingerprint);
    }

    public String getFingerprint() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "FingerprintFailedLoginOverThresholdEvent:" + getFingerprint();
    }

    @Override
    public String type() {
        return "FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD";
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FingerprintFailedLoginOverThresholdEvent that = (FingerprintFailedLoginOverThresholdEvent) o;
        return getFingerprint().equals(that.getFingerprint());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFingerprint());
    }

}
