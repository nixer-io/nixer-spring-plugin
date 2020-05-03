package io.nixer.nixerplugin.core.fingerprint;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 01/05/2020.
 *
 * @author Grzegorz Cwiak
 */
@ConfigurationProperties(prefix = "nixer.fingerprint")
public class FingerprintProperties {

    /**
     * Whether the fingerprinting feature is enabled
     */
    private boolean enabled;

    /**
     * Name of HTTP cookie to be used for storing fingerprint.
     */
    private String cookieName = "fgprt";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }
}
