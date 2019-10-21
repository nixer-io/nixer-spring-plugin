package eu.xword.nixer.nixerplugin.events;

import com.google.common.base.Objects;

/**
 * This events is emitted when number of failed login per username exceeds threshold.
 */
public class UsernameFailedLoginOverThresholdEvent extends AnomalyEvent {

    public UsernameFailedLoginOverThresholdEvent(final String username) {
        super(username);
    }

    public String getUsername() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "UsernameFailedLoginOverThresholdEvent username:" + getUsername();
    }

    @Override
    public String type() {
        return "USERNAME_FAILED_LOGIN_OVER_THRESHOLD";
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UsernameFailedLoginOverThresholdEvent that = (UsernameFailedLoginOverThresholdEvent) o;
        return getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUsername());
    }

}
