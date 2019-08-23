package eu.xword.nixer.nixerplugin.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nested in {@link ConfigurationProperties} for configuring login captcha
 */
public class CaptchaLoginProperties {

    private boolean enabled = true;

    private String strategy;

    private String param;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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
