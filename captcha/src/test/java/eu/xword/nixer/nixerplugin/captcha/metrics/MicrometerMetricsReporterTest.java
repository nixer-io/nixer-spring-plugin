package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.ACTION_TAG;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_COUNTER;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.LOGIN_ACTION;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_FAILED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_PASSED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_TAG;
import static org.assertj.core.api.Assertions.assertThat;

class MicrometerMetricsReporterTest {

    private MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());

    MicrometerMetricsReporter metricsReporter;

    @BeforeEach
    public void setup() {
        metricsReporter = new MicrometerMetricsReporter(registry, LOGIN_ACTION);
    }

    @Test
    public void shouldIncrementPassCounter() {
        metricsReporter.reportPassedCaptcha();

        assertThat(registry.get(CAPTCHA_COUNTER)
                .tag(RESULT_TAG, RESULT_PASSED)
                .tag(ACTION_TAG, LOGIN_ACTION)
                .counter().count())
                .isEqualTo(1.0);
        assertThat(registry.get(CAPTCHA_COUNTER)
                .tag(RESULT_TAG, RESULT_FAILED)
                .tag(ACTION_TAG, LOGIN_ACTION)
                .counter().count())
                .isEqualTo(0.0);
    }

    @Test
    public void shouldIncrementFailedCounter() {
        metricsReporter.reportFailedCaptcha();

        assertThat(registry.get(CAPTCHA_COUNTER)
                .tag(RESULT_TAG, RESULT_PASSED)
                .tag(ACTION_TAG, "login")
                .counter().count())
                .isEqualTo(0.0);
        assertThat(registry.get(CAPTCHA_COUNTER)
                .tag(RESULT_TAG, RESULT_FAILED)
                .tag(ACTION_TAG, LOGIN_ACTION)
                .counter().count())
                .isEqualTo(1.0);
    }
}