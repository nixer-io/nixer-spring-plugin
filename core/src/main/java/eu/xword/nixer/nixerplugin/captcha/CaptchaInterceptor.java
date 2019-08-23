package eu.xword.nixer.nixerplugin.captcha;

/**
 * Interface to intercept captcha verification process. Allowing to report metrics etc.
 */
public interface CaptchaInterceptor {

    void onCheck();

    void onSuccess();

    void onFailure();
}
