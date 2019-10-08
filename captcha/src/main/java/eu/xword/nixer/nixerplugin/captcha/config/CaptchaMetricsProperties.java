package eu.xword.nixer.nixerplugin.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * {@link ConfigurationProperties} for configuring captcha protection metrics.
 */
@Component
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
