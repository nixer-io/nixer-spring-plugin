package eu.xword.nixer.nixerplugin.metrics;

import java.util.Collections;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import org.springframework.util.Assert;

import static java.lang.String.format;

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

    // TODO handle meter type another than counter
    // TODO additional argument for value and eventually type of meter
    public void write(final String lookupId) {
        final Meter meter = meters.get(lookupId);

        if (meter == null) {
            throw new IllegalArgumentException(
                    format("Meter for lookupId '%s' not found. Available meters: '%s'", lookupId, meters.keySet()));
        }

        if (meter instanceof Counter) {
            ((Counter) meter).increment();
        } else {
            throw new UnsupportedOperationException(format("Unsupported meter type: '%s'", meter.getClass().getCanonicalName()));
        }
    }
}
