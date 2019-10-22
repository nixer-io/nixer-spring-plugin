package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import org.springframework.util.Assert;

/**
 * Provider api for accessing captcha properties
 */
public class CaptchaKeyProvider {

    private final String siteKey;

    public CaptchaKeyProvider(RecaptchaProperties recaptchaProperties) {
        Assert.notNull(recaptchaProperties, "RecaptchaProperties must not be null");
        this.siteKey = recaptchaProperties.getKey().getSite();
    }

    public String getSiteKey() {
        return siteKey;
    }
}
