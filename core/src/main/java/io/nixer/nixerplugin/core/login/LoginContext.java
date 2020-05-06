package io.nixer.nixerplugin.core.login;

import java.util.Objects;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;
import org.springframework.util.Assert;

/**
 * Stores data about user making login request.
 */
public class LoginContext {

    private final String username;

    private final String ipAddress;

    private final String userAgent;

    private final String userAgentToken;

    private final LoginResult loginResult;

    @Nullable
    private final IpMetadata ipMetadata;

    @Nullable
    private final String fingerprint;

    public LoginContext(final String username,
                        final String ipAddress,
                        final String userAgent,
                        final String userAgentToken,
                        final LoginResult loginResult,
                        @Nullable final IpMetadata ipMetadata,
                        @Nullable final String fingerprint) {
        Assert.notNull(username, "username must not be null");
        Assert.notNull(ipAddress, "ipAddress must not be null");
        Assert.notNull(userAgent, "userAgent must not be null");
        Assert.notNull(userAgentToken, "userAgentToken must not be null");
        Assert.notNull(loginResult, "loginResult must not be null");

        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.userAgentToken = userAgentToken;
        this.loginResult = loginResult;
        this.ipMetadata = ipMetadata;
        this.fingerprint = fingerprint;
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

    @Nullable
    public IpMetadata getIpMetadata() {
        return ipMetadata;
    }

    public String getUserAgentToken() {
        return userAgentToken;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    @Nullable
    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LoginContext that = (LoginContext) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(ipAddress, that.ipAddress) &&
                Objects.equals(userAgent, that.userAgent) &&
                Objects.equals(userAgentToken, that.userAgentToken) &&
                Objects.equals(fingerprint, that.fingerprint) &&
                Objects.equals(ipMetadata, that.ipMetadata) &&
                Objects.equals(loginResult, that.loginResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, ipAddress, userAgent, userAgentToken, fingerprint, ipMetadata, loginResult);
    }
}
