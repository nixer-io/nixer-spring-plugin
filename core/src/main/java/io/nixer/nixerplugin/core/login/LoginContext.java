package io.nixer.nixerplugin.core.login;

import com.google.common.base.Objects;
import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;

/**
 * Stores data about user making login request.
 */
public class LoginContext {

    private String username;

    private String ipAddress;

    private String userAgent;

    private String userAgentToken;

    private IpMetadata ipMetadata;

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    public void setIpMetadata(final IpMetadata ipMetadata) {
        this.ipMetadata = ipMetadata;
    }

    public IpMetadata getIpMetadata() {
        return ipMetadata;
    }

    public String getUserAgentToken() {
        return userAgentToken;
    }

    public void setUserAgentToken(final String userAgentToken) {
        this.userAgentToken = userAgentToken;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LoginContext that = (LoginContext) o;
        return Objects.equal(ipAddress, that.ipAddress) &&
                Objects.equal(userAgent, that.userAgent) &&
                Objects.equal(ipMetadata, that.ipMetadata) &&
                Objects.equal(userAgentToken, that.userAgentToken) &&
                Objects.equal(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ipAddress, userAgent, username, ipMetadata, userAgentToken);
    }
}
