package eu.xword.nixer.nixerplugin.metrics;

import java.util.Collections;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import org.springframework.util.Assert;

/**
 * Created on 16/10/2019.
 *
 * @author gcwiak
 */
public class MetricsFacade {

    // TODO consider moving this to a separate registry
    private final Map<String, Meter> meters;

    public MetricsFacade(final Map<String, Meter> meters) {
        Assert.notNull(meters, "meters must not be null");
        this.meters = Collections.unmodifiableMap(meters);
    }

    public void write(final String metricName) { // TODO additional argument for value and eventually type of meter
        final Meter meter = meters.get(metricName);
        // FIXME handle meter type
        // TODO handle missing meter
        ((Counter) meter).increment();
    }
}
