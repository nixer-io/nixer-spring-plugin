package eu.xword.nixer.nixerplugin.captcha.metrics;

public interface MetricsReporter {

    void reportFailedCaptcha();

    void reportPassedCaptcha();

}
