package eu.xword.nixer.nixerplugin.detection.rules;

import eu.xword.nixer.nixerplugin.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.inmemory.LoginMetric;
import org.springframework.util.Assert;

/**
 * Rule that checks if number of consecutive login failures for ip exceeds threshold and emits {@link IpFailedLoginOverThresholdEvent} event if it does.
 */
public class IpFailedLoginOverThresholdRule extends ThresholdAnomalyRule {

    private static final int THRESHOLD_VALUE = 5;

    private final LoginMetric loginMetric;

    public IpFailedLoginOverThresholdRule(final LoginMetric loginMetric) {
        super(THRESHOLD_VALUE);
        Assert.notNull(loginMetric, "LoginMetric must not be null");
        this.loginMetric = loginMetric;
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
