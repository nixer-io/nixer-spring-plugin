package eu.xword.nixer.nixerplugin.captcha.recaptcha;

/**
 * Interface for Recaptcha verification service
 */
public interface RecaptchaClient {

    RecaptchaVerifyResponse call(String captcha);
}
