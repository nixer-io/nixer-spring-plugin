package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;

/**
 * Interface for reporting eu.xword.nixer.nixerplugin.captcha metrics.
 */
public interface MetricsReporter extends CaptchaInterceptor {

    void reportFailedCaptcha();

    void reportPassedCaptcha();

}
