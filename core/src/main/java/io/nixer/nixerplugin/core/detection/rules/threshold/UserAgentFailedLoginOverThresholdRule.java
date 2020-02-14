package io.nixer.nixerplugin.core.detection.rules.threshold;

import io.nixer.nixerplugin.core.detection.events.UserAgentFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.detection.rules.EventEmitter;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.springframework.util.Assert;

/**
 * Rule that checks if number of login failures for useragent exceeds threshold and emits {@link UserAgentFailedLoginOverThresholdEvent} event if
 * it does.
 */
public class UserAgentFailedLoginOverThresholdRule extends ThresholdLoginRule {

    private static final int THRESHOLD_VALUE = 10;

    private final LoginMetric failedLoginMetric;

    public UserAgentFailedLoginOverThresholdRule(final LoginMetric failedLoginMetric) {
        super(THRESHOLD_VALUE);
        Assert.notNull(failedLoginMetric, "LoginMetric must not be null");
        this.failedLoginMetric = failedLoginMetric;
    }

    @Override
    public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
        final String userAgent = loginContext.getUserAgentToken();
        final int failedLogin = failedLoginMetric.value(userAgent);

        if (isOverThreshold(failedLogin)) {
            eventEmitter.accept(new UserAgentFailedLoginOverThresholdEvent(userAgent));
        }
    }

}
