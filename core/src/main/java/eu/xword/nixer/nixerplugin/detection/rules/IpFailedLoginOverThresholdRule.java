package eu.xword.nixer.nixerplugin.detection.rules;

import java.util.concurrent.atomic.AtomicInteger;

import eu.xword.nixer.nixerplugin.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.counts.LoginMetric;
import org.springframework.util.Assert;

/**
 * Rule that checks if number of consecutive login failures for ip exceeds threshold and emits {@link IpFailedLoginOverThresholdEvent} event if it does.
 */
public class IpFailedLoginOverThresholdRule implements Rule {

    private final AtomicInteger threshold = new AtomicInteger(5);
    private final LoginMetric loginMetric;

    public IpFailedLoginOverThresholdRule(final LoginMetric loginMetric) {
        Assert.notNull(loginMetric, "LoginMetric must not be null");
        this.loginMetric = loginMetric;
    }

    @Override
    public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
        final String ipAddress = loginContext.getIpAddress();
        final int failedLogin = loginMetric.value(ipAddress); //todo login Metric api not symmetrical.
        if (failedLogin > threshold.get()) {
            //todo duped events. should we use == ?
            eventEmitter.accept(new IpFailedLoginOverThresholdEvent(ipAddress));
        }
    }

    //todo add actuator endpoint to read/update it
    public void setThreshold(final int threshold) {
        Assert.isTrue(threshold > 1, "Threshold must be greater than 1");
        this.threshold.set(threshold);
    }
}
