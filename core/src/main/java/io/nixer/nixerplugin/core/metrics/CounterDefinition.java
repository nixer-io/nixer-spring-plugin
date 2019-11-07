package io.nixer.nixerplugin.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Created on 22/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public interface CounterDefinition {

    Counter register(final MeterRegistry meterRegistry);
}
