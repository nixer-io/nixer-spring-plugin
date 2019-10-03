package eu.xword.nixer.nixerplugin.captcha.metrics;

/**
 * Interface for reporting captcha metrics.
 */
public interface MetricsReporter {

    void reportFailedCaptcha();

    void reportPassedCaptcha();

}
