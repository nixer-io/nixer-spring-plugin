package eu.xword.nixer.nixerplugin.core.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

/**
 * Created on 18/10/2019.
 *
 * @author gcwiak
 */
public class MetersRepository {

    private final Map<String, Counter> counters;

    private MetersRepository(final Map<String, Counter> counters) {
        this.counters = Collections.unmodifiableMap(counters);
    }

    public Counter getCounter(final String lookupId) {

        final Counter counter = counters.get(lookupId);

        if (counter == null) {
            throw new IllegalArgumentException(
                    String.format("Counter for lookupId '%s' not found. Available counters: '%s'", lookupId, counters.keySet()));
        }
        return counter;
    }

    public static MetersRepository build(final List<MetersRepository.Contributor> contributors, final MeterRegistry meterRegistry) {
        Assert.notNull(contributors, "contributors must not be null");
        Assert.notNull(meterRegistry, "meterRegistry must not be null");

        final MetersRepository.Builder builder = new MetersRepository.Builder();

        contributors.forEach(contributor -> contributor.contribute(builder));

        return builder.build(meterRegistry);
    }

    public static class Builder<T extends CounterDefinition & MetricsLookupId> {

        private final List<T> counterDefinitions = new ArrayList<>();

        private Builder() {
        }

        public Builder register(T counterDefinition) {
            counterDefinitions.add(counterDefinition);
            return this;
        }

        private MetersRepository build(MeterRegistry meterRegistry) {
            final Map<String, Counter> meters = counterDefinitions
                    .stream()
                    .collect(Collectors.toMap(
                            counterDef -> counterDef.lookupId(), // do not use method reference.
                            // See javac bug https://stackoverflow.com/questions/27031244/lambdaconversionexception-with-generics-jvm-bug
                            counterDef -> counterDef.register(meterRegistry)
                    ));

            return new MetersRepository(meters);
        }
    }

    public interface Contributor {
        void contribute(Builder builder);
    }
}
