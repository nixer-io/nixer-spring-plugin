package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.ActionExecutingMetricsReporter;
import eu.xword.nixer.nixerplugin.metrics.MetricsWriter;

import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.NOT_PWNED_PASSWORD;
import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.PWNED_PASSWORD;

/**
 * Created on 18/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class PwnedPasswordMetricsReporter extends ActionExecutingMetricsReporter<Boolean> {

    public PwnedPasswordMetricsReporter(final MetricsWriter metricsWriter) {
        super(metricsWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeMetrics(final MetricsWriter metricsWriter, final Boolean isPasswordPwned) {
        metricsWriter.write(isPasswordPwned ? PWNED_PASSWORD : NOT_PWNED_PASSWORD);
    }
}
