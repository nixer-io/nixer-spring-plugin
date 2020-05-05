package io.nixer.nixerplugin.core.login;

import java.util.Objects;

import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;

/**
 * Stores data about user making login request.
 */
public class LoginContext {

    private String username;

    private String ipAddress;

    private String userAgent;

    private String userAgentToken;

    private String fingerprint;

    private IpMetadata ipMetadata;

    private LoginResult loginResult;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    public IpMetadata getIpMetadata() {
        return ipMetadata;
    }

    public void setIpMetadata(final IpMetadata ipMetadata) {
        this.ipMetadata = ipMetadata;
    }

    public String getUserAgentToken() {
        return userAgentToken;
    }

    public void setUserAgentToken(final String userAgentToken) {
        this.userAgentToken = userAgentToken;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(final LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(final String fingerprint) {
        this.fingerprint = fingerprint;
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
