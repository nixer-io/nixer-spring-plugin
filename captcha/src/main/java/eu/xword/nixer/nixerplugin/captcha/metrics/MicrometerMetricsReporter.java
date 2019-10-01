package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.ACTION_TAG;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_COUNTER;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_FAILED_DESC;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_PASS_DESC;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_FAILED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_PASSED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_TAG;

/**
 * Reports captcha metrics to micrometer's {@link MeterRegistry}.
 */
public class MicrometerMetricsReporter implements MetricsReporter {

    private final Counter captchaPassedCounter;

    private final Counter captchaFailedCounter;

    public MicrometerMetricsReporter(final MeterRegistry meterRegistry, final String action) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");
        Assert.notNull(action, "Action must not be null");

        this.captchaPassedCounter = Counter.builder(CAPTCHA_COUNTER)
                .description(CAPTCHA_PASS_DESC)
                .tag(RESULT_TAG, RESULT_PASSED)
                .tag(ACTION_TAG, action)
                .register(meterRegistry);

        this.captchaFailedCounter = Counter.builder(CAPTCHA_COUNTER)
                .description(CAPTCHA_FAILED_DESC)
                .tag(RESULT_TAG, RESULT_FAILED)
                .tag(ACTION_TAG, action)
                .register(meterRegistry);
    }

    public void reportFailedCaptcha() {
        this.captchaFailedCounter.increment();
    }

    public void reportPassedCaptcha() {
        this.captchaPassedCounter.increment();
    }

    @Override
    public void onCheck() {

    }

    @Override
    public void onSuccess() {
        reportPassedCaptcha();
    }

    @Override
    public void onFailure() {
        reportFailedCaptcha();
    }
}
