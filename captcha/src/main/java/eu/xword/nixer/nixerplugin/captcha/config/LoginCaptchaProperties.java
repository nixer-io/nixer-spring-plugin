package eu.xword.nixer.nixerplugin.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nested in {@link ConfigurationProperties} for configuring login captcha
 */
@ConfigurationProperties(prefix = "nixer.login.captcha")
public class LoginCaptchaProperties {

    public static final String DEFAULT_CAPTCHA_PARAM = "g-recaptcha-response";

    /**
     * Determines at what condition captcha applies
     */
    private String condition;
    /**
     * Name of Http parameter name containing captcha response
     */
    private String param = DEFAULT_CAPTCHA_PARAM;

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
