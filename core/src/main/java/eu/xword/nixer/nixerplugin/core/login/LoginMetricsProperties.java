package eu.xword.nixer.nixerplugin.core.login;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.login.metrics")
public class LoginMetricsProperties {

    public static final boolean DEFAULT = true;
    private boolean enabled = DEFAULT;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}