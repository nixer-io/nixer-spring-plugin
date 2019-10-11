package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.ACTION_TAG;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_FAILED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_METRIC;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.CAPTCHA_PASSED;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.LOGIN_ACTION;
import static eu.xword.nixer.nixerplugin.captcha.metrics.CaptchaMetrics.RESULT_TAG;
import static org.assertj.core.api.Assertions.assertThat;

class MicrometerMetricsReporterTest {

    private MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
    private MicrometerMetricsReporter metricsReporter = new MicrometerMetricsReporter(registry, LOGIN_ACTION);

    @Test
    public void shouldIncrementPassCounter() {
        assertThat(passCaptchaCounter().count())
                .isEqualTo(0.0);
        assertThat(failedCaptchaCounter().count())
                .isEqualTo(0.0);

        metricsReporter.reportPassedCaptcha();

        assertThat(passCaptchaCounter().count())
                .isEqualTo(1.0);
        assertThat(failedCaptchaCounter().count())
                .isEqualTo(0.0);
    }

    @Test
    public void shouldIncrementFailedCounter() {
        assertThat(passCaptchaCounter().count())
                .isEqualTo(0.0);
        assertThat(failedCaptchaCounter().count())
                .isEqualTo(0.0);

        metricsReporter.reportFailedCaptcha();

        assertThat(passCaptchaCounter().count())
                .isEqualTo(0.0);
        assertThat(failedCaptchaCounter().count())
                .isEqualTo(1.0);
    }

    private Counter passCaptchaCounter() {
        return registry.get(CAPTCHA_METRIC)
                .tag(RESULT_TAG, CAPTCHA_PASSED)
                .tag(ACTION_TAG, LOGIN_ACTION)
                .counter();
    }

    private Counter failedCaptchaCounter() {
        return registry.get(CAPTCHA_METRIC)
                .tag(RESULT_TAG, CAPTCHA_FAILED)
                .tag(ACTION_TAG, LOGIN_ACTION)
                .counter();
    }
}