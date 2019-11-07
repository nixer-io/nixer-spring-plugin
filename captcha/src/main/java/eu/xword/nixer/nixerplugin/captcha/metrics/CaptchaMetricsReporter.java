package eu.xword.nixer.nixerplugin.captcha.metrics;

import eu.xword.nixer.nixerplugin.core.metrics.MetricsCounter;
import eu.xword.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaCounters.CAPTCHA_FAILED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaCounters.CAPTCHA_PASSED;

/**
 * Reports captcha metrics.
 */
public class CaptchaMetricsReporter {

    public static final String LOGIN_ACTION = "login";

    private final MetricsCounter passedCaptchaCounter;
    private final MetricsCounter failedCaptchaCounter;

    CaptchaMetricsReporter(final MetricsCounter passedCaptchaCounter, final MetricsCounter failedCaptchaCounter) {
        Assert.notNull(passedCaptchaCounter, "PassedCaptchaCounter must not be null");
        this.passedCaptchaCounter = passedCaptchaCounter;

        Assert.notNull(failedCaptchaCounter, "FailedCaptchaCounter must not be null");
        this.failedCaptchaCounter = failedCaptchaCounter;
    }

    public void reportFailedCaptcha() {
        this.failedCaptchaCounter.increment();
    }

    public void reportPassedCaptcha() {
        this.passedCaptchaCounter.increment();
    }


    public static CaptchaMetricsReporter create(final MetricsFactory metricsFactory, final String action) {
        Assert.notNull(metricsFactory, "MetricsFactory must not be null");
        Assert.notNull(action, "Action must not be null");

        final MetricsCounter passCounter = metricsFactory.counter(CAPTCHA_PASSED.counter(action));
        final MetricsCounter failureCounter = metricsFactory.counter(CAPTCHA_FAILED.counter(action));

        return new CaptchaMetricsReporter(passCounter, failureCounter);
    }
}
