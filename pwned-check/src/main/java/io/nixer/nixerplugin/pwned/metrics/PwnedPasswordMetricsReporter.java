package io.nixer.nixerplugin.pwned.metrics;

import java.util.function.Supplier;

import io.nixer.nixerplugin.core.metrics.MetricsCounter;
import io.nixer.nixerplugin.core.metrics.MetricsFactory;
import org.springframework.util.Assert;

import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.NOT_PWNED_PASSWORD;
import static io.nixer.nixerplugin.pwned.metrics.PwnedCheckCounters.PWNED_PASSWORD;

/**
 * Reports metrics about pwned password check
 * Created on 18/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class PwnedPasswordMetricsReporter {

    private final MetricsCounter pwnedPasswordCounter;
    private final MetricsCounter notPwnedPasswordCounter;

    protected PwnedPasswordMetricsReporter(final MetricsCounter pwnedPasswordCounter, final MetricsCounter notPwnedPasswordCounter) {
        Assert.notNull(pwnedPasswordCounter, "pwnedPasswordCounter must not be null");
        this.pwnedPasswordCounter = pwnedPasswordCounter;

        Assert.notNull(notPwnedPasswordCounter, "notPwnedPasswordCounter must not be null");
        this.notPwnedPasswordCounter = notPwnedPasswordCounter;
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

        (result ? pwnedPasswordCounter : notPwnedPasswordCounter).increment();

        return result;
    }

    private Boolean execute(final Supplier<Boolean> actionGivingResult) {
        return actionGivingResult.get();
    }

    public static PwnedPasswordMetricsReporter create(final MetricsFactory metricsFactory) {
        Assert.notNull(metricsFactory, "metricsFactory must not be null");

        return new PwnedPasswordMetricsReporter(
                metricsFactory.counter(PwnedCheckCounters.PWNED_PASSWORD),
                metricsFactory.counter(PwnedCheckCounters.NOT_PWNED_PASSWORD)
        );
    }
}
