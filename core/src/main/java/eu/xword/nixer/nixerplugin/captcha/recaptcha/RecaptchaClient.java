package eu.xword.nixer.nixerplugin.captcha.recaptcha;

/**
 * Interface for
 */
public interface RecaptchaClient {

    RecaptchaVerifyResponse call(String captcha);
}
