package eu.xword.nixer.nixerplugin.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Created on 22/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public interface CounterDefinition extends MetricsLookupId {

    Counter register(final MeterRegistry meterRegistry);
}
