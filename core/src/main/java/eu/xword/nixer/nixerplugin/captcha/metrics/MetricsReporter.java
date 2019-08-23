package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;

/**
 * Interface for reporting captcha metrics.
 */
public interface MetricsReporter extends CaptchaInterceptor {

    void reportFailedCaptcha();

    void reportPassedCaptcha();

}
