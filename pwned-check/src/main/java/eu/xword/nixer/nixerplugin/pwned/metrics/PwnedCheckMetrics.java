package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.CounterDefinition;
import eu.xword.nixer.nixerplugin.metrics.MetricsLookupId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public enum PwnedCheckMetrics implements MetricsLookupId, CounterDefinition {

    PWNED_PASSWORD(
            "Password is pwned",
            "pwned_password"
    ),

    NOT_PWNED_PASSWORD(
            "Password is not pwned",
            "not_pwned_password"
    );

    public final String metricName = "pwned_check";
    public final String resultTag = "result";
    public final String description;
    public final String result;

    PwnedCheckMetrics(final String description, final String result) {
        this.description = description;
        this.result = result;
    }

    @Override
    public String lookupId() {
        return name();
    }

    @Override
    public Counter register(final MeterRegistry meterRegistry) {
        return Counter.builder(metricName)
                .description(description)
                .tag(resultTag, result)
                .register(meterRegistry);
    }
}
