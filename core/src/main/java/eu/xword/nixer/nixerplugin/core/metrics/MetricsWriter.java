package eu.xword.nixer.nixerplugin.core.metrics;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public interface MetricsWriter {

    void write(MetricsLookupId metricsLookupId);
}
