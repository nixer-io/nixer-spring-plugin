package eu.xword.nixer.nixerplugin.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static java.lang.String.format;

/**
 * Created on 16/10/2019.
 *
 * @author gcwiak
 */
@Component
public class MetricsFacade {

    private final MetersRepository metersRepository;

    public MetricsFacade(final MetersRepository metersRepository) {
        Assert.notNull(metersRepository, "metersRepository must not be null");
        this.metersRepository = metersRepository;
    }

    // TODO handle meter type another than counter
    // TODO additional argument for value and eventually type of meter
    public void write(final String lookupId) {
        final Meter meter = metersRepository.get(lookupId);

        if (meter instanceof Counter) {
            ((Counter) meter).increment();
        } else {
            throw new UnsupportedOperationException(format("Unsupported meter type: '%s'", meter.getClass().getCanonicalName()));
        }
    }
}
