package eu.xword.nixer.nixerplugin.pwned.metrics;

import java.util.function.Supplier;

import eu.xword.nixer.nixerplugin.metrics.MetricsWriter;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.NOT_PWNED_PASSWORD;
import static eu.xword.nixer.nixerplugin.pwned.metrics.PwnedCheckMetrics.PWNED_PASSWORD;

/**
 * Created on 18/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class PwnedPasswordMetricsReporter {

    private final MetricsWriter metricsWriter;

    public PwnedPasswordMetricsReporter(final MetricsWriter metricsWriter) {
        Assert.notNull(metricsWriter, "metricsWriter must not be null");
        this.metricsWriter = metricsWriter;
    }

    /**
     * Executes the given action, reports metrics basing on the result and returns the result.
     *
     * @param pwnedPasswordCheckAction
     * @return result of the action
     */
    public final Boolean report(Supplier<Boolean> pwnedPasswordCheckAction) {
        Assert.notNull(pwnedPasswordCheckAction, "pwnedPasswordCheckAction must not be null");

        final Boolean result = execute(pwnedPasswordCheckAction);

        metricsWriter.write(result ? PWNED_PASSWORD : NOT_PWNED_PASSWORD);

        return result;
    }

    private Boolean execute(final Supplier<Boolean> actionGivingResult) {
        return actionGivingResult.get();
    }

}
