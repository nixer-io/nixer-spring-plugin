package eu.xword.nixer.nixerplugin.metrics;

import java.util.function.Function;
import java.util.function.Supplier;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Created on 16/10/2019.
 *
 * @author gcwiak
 */
public class MeterDefinition {

    private final String lookupId;

    private final Function<MeterRegistry, Meter> definition;

    public static MeterDefinition counter(final String lookupId, final Supplier<Counter.Builder> counterDefinition) {
        return new MeterDefinition(lookupId, counterDefinition.get()::register);
    }

    private MeterDefinition(final String lookupId, final Function<MeterRegistry, Meter> definition) {
        this.lookupId = lookupId;
        this.definition = definition;
    }

    public String getLookupId() {
        return lookupId;
    }

    public Meter register(final MeterRegistry meterRegistry) {
        return definition.apply(meterRegistry);
    }
}
