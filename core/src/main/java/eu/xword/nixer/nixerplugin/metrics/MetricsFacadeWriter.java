package eu.xword.nixer.nixerplugin.metrics;

import org.springframework.util.Assert;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public class MetricsFacadeWriter implements MetricsWriter {

    private final MetricsFacade metricsFacade;

    public MetricsFacadeWriter(final MetricsFacade metricsFacade) {
        Assert.notNull(metricsFacade, "metricsFacade must not be null");
        this.metricsFacade = metricsFacade;
    }

    @Override
    public void write(final String metricName) {
        metricsFacade.write(metricName);
    }
}
