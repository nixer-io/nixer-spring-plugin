package eu.xword.nixer.nixerplugin.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

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

    public static class Builder {
        private final List<MeterDefinition> meterDefinitions = new ArrayList<>();

        Builder() {
        }

        public Builder register(MeterDefinition meterDefinition) {
            meterDefinitions.add(meterDefinition);
            return this;
        }

        MetersRepository build(MeterRegistry meterRegistry) {
            final Map<String, Meter> meters = meterDefinitions.stream()
                    .collect(Collectors.toMap(
                            MeterDefinition::getLookupId,
                            meterDefinition -> meterDefinition.register(meterRegistry)
                    ));

            return new MetersRepository(meters);
        }
    }

    public interface Contributor {
        void contribute(Builder builder);
    }
}
