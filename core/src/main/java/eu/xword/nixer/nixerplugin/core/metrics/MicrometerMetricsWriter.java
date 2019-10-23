package eu.xword.nixer.nixerplugin.core.metrics;

import org.springframework.util.Assert;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public class MicrometerMetricsWriter implements MetricsWriter {

    private final MetersRepository metersRepository;

    public MicrometerMetricsWriter(final MetersRepository metersRepository) {
        Assert.notNull(metersRepository, "metersRepository must not be null");
        this.metersRepository = metersRepository;
    }

    // TODO handle meter type another than counter
    // TODO additional argument for value and eventually type of meter
    @Override
    public void write(final MetricsLookupId metricsLookupId) {
        metersRepository
                .getCounter(metricsLookupId.lookupId())
                .increment();
    }
}
