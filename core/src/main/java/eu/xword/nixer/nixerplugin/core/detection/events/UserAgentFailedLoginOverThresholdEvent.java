package eu.xword.nixer.nixerplugin.core.detection.events;

import com.google.common.base.Objects;

/**
 * This events is emitted when number of failed login per useragent exceeds threshold.
 */
public class UserAgentFailedLoginOverThresholdEvent extends AnomalyEvent {

    public UserAgentFailedLoginOverThresholdEvent(final String userAgent) {
        super(userAgent);
    }

    public String getUserAgent() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "UserAgentFailedLoginOverThresholdEvent userAgent:" + getUserAgent();
    }

    @Override
    public String type() {
        return "USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD";
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserAgentFailedLoginOverThresholdEvent that = (UserAgentFailedLoginOverThresholdEvent) o;
        return getUserAgent().equals(that.getUserAgent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserAgent());
    }

}
