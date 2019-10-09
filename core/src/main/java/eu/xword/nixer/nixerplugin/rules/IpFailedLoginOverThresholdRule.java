package eu.xword.nixer.nixerplugin.rules;

import java.util.concurrent.atomic.AtomicInteger;

import eu.xword.nixer.nixerplugin.events.IpFailedLoginOverThresholdEvent;
import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.counts.IpCountStore;
import org.springframework.util.Assert;

/**
 * Rule that triggers if number of consecutive login failures for ip exceeds threshold and emits {@link IpFailedLoginOverThresholdEvent} event if it does.
 */
public class IpFailedLoginOverThresholdRule implements Rule {

    private final AtomicInteger threshold = new AtomicInteger(5);
    private final IpCountStore ipCountStore;

    public IpFailedLoginOverThresholdRule(final IpCountStore ipCountStore) {
        this.ipCountStore = ipCountStore;
    }

    @Override
    public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
        final String ipAddress = loginContext.getIpAddress();
        final int failedLogin = ipCountStore.failedLoginByIp(ipAddress);
        if (failedLogin > threshold.get()) {
            //todo duped events. should we use == ?
            eventEmitter.emit(new IpFailedLoginOverThresholdEvent(ipAddress));
        }
    }

    public void setThreshold(final int threshold) {
        Assert.isTrue(threshold > 1, "Threshold must be greater than 1");
        this.threshold.set(threshold);
    }
}
