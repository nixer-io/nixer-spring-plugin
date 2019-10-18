package eu.xword.nixer.nixerplugin.metrics;

import java.util.function.Supplier;

import org.springframework.util.Assert;

/**
 * <p>
 * Base class for metrics reporters that execute a given action,
 * report metrics according to the execution result and return the result.
 * </p>
 * <p>
 * It allows separating details of metrics reporting from the action being measured.
 * </p>
 * <p>
 * Subclasses are to specify how the metrics should be reported by implementing
 * {@link ActionExecutingMetricsReporter#writeMetrics(MetricsWriter, Object)} template method.
 * </p>
 *
 * Created on 18/10/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public abstract class ActionExecutingMetricsReporter<RESULT> {

    private final MetricsWriter metricsWriter;

    protected ActionExecutingMetricsReporter(final MetricsWriter metricsWriter) {
        Assert.notNull(metricsWriter, "metricsWriter must not be null");
        this.metricsWriter = metricsWriter;
    }

    public final RESULT executeAndReport(Supplier<RESULT> actionGivingResult) {
        Assert.notNull(actionGivingResult, "actionGivingResult must not be null");

        final RESULT result = execute(actionGivingResult);

        writeMetrics(metricsWriter, result);

        return result;
    }

    private RESULT execute(final Supplier<RESULT> actionGivingResult) {
        return actionGivingResult.get();
    }

    /**
     * Specifies how metrics should be reported basing on the action result.
     *
     * @param metricsWriter
     * @param result
     */
    protected abstract void writeMetrics(final MetricsWriter metricsWriter, final RESULT result);
}
