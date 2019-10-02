package eu.xword.nixer.nixerplugin.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nested in {@link ConfigurationProperties} for configuring login captcha
 */
@ConfigurationProperties(prefix = "nixer.login.captcha")
public class LoginCaptchaProperties {

    public static final String DEFAULT_CAPTCHA_PARAM = "g-recaptcha-response";

    public static final boolean DEFAULT = true;
    private boolean enabled = DEFAULT;

    private String condition;
    /**
     * Name of Http parameter name containing captcha response
     */
    private String param = DEFAULT_CAPTCHA_PARAM;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(final String condition) {
        this.condition = condition;
    }

    public String getParam() {
        return param;
    }

    public void setParam(final String param) {
        this.param = param;
    }

}
