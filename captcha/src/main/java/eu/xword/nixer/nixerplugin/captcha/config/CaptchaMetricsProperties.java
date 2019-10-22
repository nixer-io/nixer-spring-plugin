package eu.xword.nixer.nixerplugin.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for configuring captcha protection metrics.
 */
@ConfigurationProperties(prefix = "nixer.captcha")
public class CaptchaMetricsProperties {

    public static final boolean DEFAULT = true;

    /**
     * Whether captcha metrics should be reported or not
     */
    private boolean enabled = DEFAULT;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
