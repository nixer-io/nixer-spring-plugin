package eu.xword.nixer.nixerplugin.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created on 16/10/2019.
 *
 * @author gcwiak
 */
@Component
public class MetricsFacade {

    // TODO consider using more abstract metric implementation here, e.g. Meter, instead of Counter
    // TODO move this to a separate registry, e.g. meters registry
    private final Map<String, Counter> meters;

    public MetricsFacade(final MeterRegistry meterRegistry) {
        Assert.notNull(meterRegistry, "MeterRegistry must not be null");

        // TODO move this registering outside
        HashMap<String, Counter> aMeters = new HashMap<>();

        aMeters.put(
                "pwned_password_positive",
                Counter.builder("pwned_password")
                        .description("Password is pwned")
                        .tag("result", "positive")
                        .register(meterRegistry)
        );

        aMeters.put(
                "pwned_password_negative",
                Counter.builder("pwned_password")
                        .description("Password is not pwned")
                        .tag("result", "negative")
                        .register(meterRegistry)
        );

        this.meters = Collections.unmodifiableMap(aMeters);
    }

    public void write(final String metricName) { // TODO additional argument for value and eventually type of meter
        meters.get(metricName).increment(); // TODO handle missing meter
    }
}
