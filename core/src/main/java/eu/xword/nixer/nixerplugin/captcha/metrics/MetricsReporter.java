package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.captcha.CaptchaInterceptor;

public interface MetricsReporter extends CaptchaInterceptor {

    void reportFailedCaptcha();

    void reportPassedCaptcha();

}
