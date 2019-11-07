package io.nixer.nixerplugin.core.metrics;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Creates objects representing metrics
 */
public abstract class MetricsFactory {

    private static final DisabledCounter DISABLED_COUNTER = new DisabledCounter();

    public abstract MetricsCounter counter(final CounterDefinition counterDefinition);

    public static MetricsFactory createNullFactory() {
        return new MetricsFactory() {

            @Override
            public MetricsCounter counter(final CounterDefinition counterDefinition) {
                return DISABLED_COUNTER;
            }
        };
    }

    public static MetricsFactory create(final MeterRegistry meterRegistry) {
        return new MicrometerMetricsFactory(meterRegistry);
    }

    private static final class DisabledCounter implements MetricsCounter {

        @Override
        public void increment() {

        }
    }
}
