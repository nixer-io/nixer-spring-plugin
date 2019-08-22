package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class MicrometerMetricsReporter implements MetricsReporter {

    private final Counter captchaPassedCounter;

    private final Counter captchaFailedCounter;

    public MicrometerMetricsReporter(final MeterRegistry meterRegistry, final String action) {
        this.captchaPassedCounter = Counter.builder("recaptcha")
                .description("Captcha passes")
                .tag("result", "passed")
                .tag("action", action)
                .register(meterRegistry);


        this.captchaFailedCounter = Counter.builder("recaptcha")
                .description("Captcha failed")
                .tag("result", "failed")
                .tag("action", action)
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
