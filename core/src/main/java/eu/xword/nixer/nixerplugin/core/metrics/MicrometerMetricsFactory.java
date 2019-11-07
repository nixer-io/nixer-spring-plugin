package eu.xword.nixer.nixerplugin.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

final class MicrometerMetricsFactory extends MetricsFactory {

    private final MeterRegistry meterRegistry;

    MicrometerMetricsFactory(final MeterRegistry meterRegistry) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");
        this.meterRegistry = meterRegistry;
    }

    @Override
    public MetricsCounter counter(final CounterDefinition counterDefinition) {
        Assert.notNull(counterDefinition, "CounterDefinition must not be null");

        return new ActiveCounter(counterDefinition.register(meterRegistry));
    }

    private final class ActiveCounter implements MetricsCounter {

        private final Counter counter;

        private ActiveCounter(final Counter counter) {
            Assert.notNull(counter, "Counter must not be null");
            this.counter = counter;
        }

        @Override
        public void increment() {
            counter.increment();
        }
    }
}
