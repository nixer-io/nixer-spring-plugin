package eu.xword.nixer.nixerplugin.core.events;

import com.google.common.base.Objects;

/**
 * This events is emitted when number of failed login per ip exceeds threshold.
 */
public class IpFailedLoginOverThresholdEvent extends AnomalyEvent {

    public IpFailedLoginOverThresholdEvent(final String ip) {
        super(ip);
    }

    public String getIp() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "IpFailedLoginOverThresholdEvent ip:" + getIp();
    }

    @Override
    public String type() {
        return "IP_FAILED_LOGIN_OVER_THRESHOLD";
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IpFailedLoginOverThresholdEvent that = (IpFailedLoginOverThresholdEvent) o;
        return getIp().equals(that.getIp());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIp());
    }

}
