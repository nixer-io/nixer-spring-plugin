package eu.xword.nixer.nixerplugin.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nested in {@link ConfigurationProperties} for configuring login captcha
 */
public class CaptchaLoginProperties {

    private boolean enabled = true;

    private boolean enableMetrics;

    private String strategy;

    private String param;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnableMetrics() {
        return enableMetrics;
    }

    public void setEnableMetrics(final boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(final String strategy) {
        this.strategy = strategy;
    }

    public String getParam() {
        return param;
    }

    public void setParam(final String param) {
        this.param = param;
    }

}
