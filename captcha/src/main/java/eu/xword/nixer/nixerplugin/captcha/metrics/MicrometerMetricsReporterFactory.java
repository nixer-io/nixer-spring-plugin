package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

/**
 * Creates instance of {@link MetricsReporter} based-up by micrometer.
 */
public class MicrometerMetricsReporterFactory implements MetricsReporterFactory {

    private final MeterRegistry meterRegistry;

    public MicrometerMetricsReporterFactory(final MeterRegistry meterRegistry) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public MetricsReporter createMetricsReporter(final String action) {
        return new MicrometerMetricsReporter(meterRegistry, action);
    }
}
