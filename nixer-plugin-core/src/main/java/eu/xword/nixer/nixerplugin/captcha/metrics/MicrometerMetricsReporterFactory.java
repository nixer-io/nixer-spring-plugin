package eu.xword.nixer.nixerplugin.captcha.metrics;

import io.micrometer.core.instrument.MeterRegistry;

public class MicrometerMetricsReporterFactory implements MetricsReporterFactory {

    private final MeterRegistry meterRegistry;

    public MicrometerMetricsReporterFactory(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public MetricsReporter createMetricsReporter(final String action) {
        return new MicrometerMetricsReporter(meterRegistry, action);
    }
}
