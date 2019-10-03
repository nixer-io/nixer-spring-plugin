package eu.xword.nixer.nixerplugin.captcha.metrics;

/**
 * Null-Object for {@link MetricsReporter}. Simply ignores reported metrics.
 */
public class NOPMetricsReporter implements MetricsReporter {
    @Override
    public void reportFailedCaptcha() {

    }

    @Override
    public void reportPassedCaptcha() {

    }

}
