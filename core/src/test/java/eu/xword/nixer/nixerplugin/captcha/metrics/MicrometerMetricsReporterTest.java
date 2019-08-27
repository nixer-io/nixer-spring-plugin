package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MicrometerMetricsReporterTest {

    private MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());

    MicrometerMetricsReporter metricsReporter;

    @BeforeEach
    public void setup() {
        metricsReporter = new MicrometerMetricsReporter(registry, "login");
    }

    @Test
    public void shouldIncrementPassCounter() {
        metricsReporter.reportPassedCaptcha();

        assertThat(registry.get("recaptcha")
                .tag("result", "passed")
                .tag("action", "login")
                .counter().count())
                .isEqualTo(1.0);
        assertThat(registry.get("recaptcha")
                .tag("result", "failed")
                .tag("action", "login")
                .counter().count())
                .isEqualTo(0.0);
    }

    @Test
    public void shouldIncrementFailedCounter() {
        metricsReporter.reportFailedCaptcha();

        assertThat(registry.get("recaptcha")
                .tag("result", "passed")
                .tag("action", "login")
                .counter().count())
                .isEqualTo(0.0);
        assertThat(registry.get("recaptcha")
                .tag("result", "failed")
                .tag("action", "login")
                .counter().count())
                .isEqualTo(1.0);
    }
}