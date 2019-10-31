package eu.xword.nixer.nixerplugin.core.detection.rules.threshold;

import eu.xword.nixer.nixerplugin.core.detection.rules.EventEmitter;
import eu.xword.nixer.nixerplugin.core.detection.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.springframework.util.Assert;

/**
 * Rule that checks if number of consecutive login failures for ip exceeds threshold and emits {@link IpFailedLoginOverThresholdEvent} event if it does.
 */
public class IpFailedLoginOverThresholdRule extends ThresholdAnomalyRule {

    private static final int THRESHOLD_VALUE = 5;

    private final LoginMetric loginMetric;

    public IpFailedLoginOverThresholdRule(final LoginMetric failedLoginMetric) {
        super(THRESHOLD_VALUE);
        Assert.notNull(failedLoginMetric, "LoginMetric must not be null");
        this.loginMetric = failedLoginMetric;
    }

    @Override
    public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
        final String ipAddress = loginContext.getIpAddress();
        final int failedLogin = loginMetric.value(ipAddress); //todo login Metric api not symmetrical.

        if (isOverThreshold(failedLogin)) {
            eventEmitter.accept(new IpFailedLoginOverThresholdEvent(ipAddress));
        }
    }

}
