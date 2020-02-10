package io.nixer.nixerplugin.captcha.config;

import io.nixer.nixerplugin.captcha.security.CaptchaCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nested in {@link ConfigurationProperties} for configuring login captcha
 */
@ConfigurationProperties(prefix = "nixer.login.captcha")
public class LoginCaptchaProperties {

    public static final String DEFAULT_CAPTCHA_PARAM = "g-recaptcha-response";

    /**
     * Determines whether captcha challenge applies
     */
    private CaptchaCondition condition;
    /**
     * Name of Http parameter name containing captcha response
     */
    private String param = DEFAULT_CAPTCHA_PARAM;

    public CaptchaCondition getCondition() {
        return condition;
    }

    public void setCondition(final CaptchaCondition condition) {
        this.condition = condition;
    }

    public String getParam() {
        return param;
    }

    public void setParam(final String param) {
        this.param = param;
    }

}
