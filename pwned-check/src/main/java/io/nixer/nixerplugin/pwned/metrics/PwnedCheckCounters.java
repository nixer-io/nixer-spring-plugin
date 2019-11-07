package io.nixer.nixerplugin.pwned.metrics;

import io.nixer.nixerplugin.core.metrics.CounterDefinition;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Defines counters reported for pwned password check
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public enum PwnedCheckCounters implements CounterDefinition {

    PWNED_PASSWORD {
        @Override
        public Counter register(final MeterRegistry meterRegistry) {
            return Counter.builder(METRIC_NAME)
                    .description("Password is pwned")
                    .tag(RESULT_TAG, PWNED_RESULT)
                    .register(meterRegistry);
        }
    },

    NOT_PWNED_PASSWORD {
        @Override
        public Counter register(final MeterRegistry meterRegistry) {
            return Counter.builder(METRIC_NAME)
                    .description("Password is not pwned")
                    .tag(RESULT_TAG, NOT_PWNED_RESULT)
                    .register(meterRegistry);
        }
    };

    public static final String NOT_PWNED_RESULT = "not_pwned_password";
    public static final String PWNED_RESULT = "pwned_password";
    public static final String METRIC_NAME = "pwned_check";
    public static final String RESULT_TAG = "result";
}
