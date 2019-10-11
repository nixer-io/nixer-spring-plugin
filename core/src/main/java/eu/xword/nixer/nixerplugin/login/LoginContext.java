package eu.xword.nixer.nixerplugin.login;

import com.google.common.base.Objects;
import eu.xword.nixer.nixerplugin.ip.IpMetadata;

/**
 * Stores data about user making login request.
 */
public class LoginContext {

    //TODO we need to keep info if username was valid.
    // 1. to be used for metrics eg. unique usernames/ip
    // 1. to be used for security. Tracking metrics / username could leak to DOS.
    //      Attacker controls that field and could generate random names.
    private final String username;

    private final String ipAddress;

    private final String userAgent;

    private IpMetadata ipMetadata;

    public LoginContext(final String username, final String ipAddress, final String userAgent) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setIpMetadata(final IpMetadata ipMetadata) {
        this.ipMetadata = ipMetadata;
    }

    public IpMetadata getIpMetadata() {
        return ipMetadata;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LoginContext that = (LoginContext) o;
        return Objects.equal(ipAddress, that.ipAddress) &&
                Objects.equal(userAgent, that.userAgent) &&
                Objects.equal(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ipAddress, userAgent, username);
    }
}
