package eu.xword.nixer.nixerplugin.captcha.recaptcha;

/**
 * Interface for Recaptcha verification API
 */
public interface RecaptchaClient {

    RecaptchaVerifyResponse call(final String captcha);
}
