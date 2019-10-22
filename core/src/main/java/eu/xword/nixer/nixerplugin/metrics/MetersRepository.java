package eu.xword.nixer.nixerplugin.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.Assert;

/**
 * Created on 18/10/2019.
 *
 * @author gcwiak
 */
public class MetersRepository {

    private final Map<String, Meter> meters;

    private MetersRepository(final Map<String, Meter> meters) {
        this.meters = Collections.unmodifiableMap(meters);
    }

    public Meter get(final String lookupId) {

        final Meter meter = meters.get(lookupId);

        if (meter == null) {
            throw new IllegalArgumentException(
                    String.format("Meter for lookupId '%s' not found. Available meters: '%s'", lookupId, meters.keySet()));
        }
        return meter;
    }

    public static MetersRepository build(final List<MetersRepository.Contributor> contributors, final MeterRegistry meterRegistry) {
        Assert.notNull(contributors, "contributors must not be null");
        Assert.notNull(meterRegistry, "meterRegistry must not be null");

        final MetersRepository.Builder builder = new MetersRepository.Builder();

        contributors.forEach(contributor -> contributor.contribute(builder));

        return builder.build(meterRegistry);
    }

    public static class Builder {

        private final List<CounterDefinition> counterDefinitions = new ArrayList<>();

        private Builder() {
        }

        public Builder register(CounterDefinition counterDefinition) {
            counterDefinitions.add(counterDefinition);
            return this;
        }

        private MetersRepository build(MeterRegistry meterRegistry) {
            final Map<String, Meter> meters = counterDefinitions.stream()
                    .collect(Collectors.toMap(
                            MetricsLookupId::lookupId,
                            counterDefinition -> counterDefinition.register(meterRegistry)
                    ));

            return new MetersRepository(meters);
        }
    }

    public interface Contributor {
        void contribute(Builder builder);
    }
}
