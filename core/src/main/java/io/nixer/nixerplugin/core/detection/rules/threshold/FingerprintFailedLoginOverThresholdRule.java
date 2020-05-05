package io.nixer.nixerplugin.core.detection.rules.threshold;

import io.nixer.nixerplugin.core.detection.events.FingerprintFailedLoginOverThresholdEvent;
import io.nixer.nixerplugin.core.detection.rules.EventEmitter;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.inmemory.LoginMetric;
import org.springframework.util.Assert;

public class FingerprintFailedLoginOverThresholdRule extends ThresholdLoginRule {

    static final int THRESHOLD_VALUE = 5;

    private final LoginMetric loginMetric;

    public FingerprintFailedLoginOverThresholdRule(final LoginMetric failedLoginMetric) {
        super(THRESHOLD_VALUE);
        Assert.notNull(failedLoginMetric, "LoginMetric must not be null");
        this.loginMetric = failedLoginMetric;
    }

    @Override
    public void execute(final LoginContext loginContext, final EventEmitter eventEmitter) {
        final String fingerprint = loginContext.getFingerprint();
        final int failedLogin = loginMetric.value(fingerprint);

        if (isOverThreshold(failedLogin)) {
            eventEmitter.accept(new FingerprintFailedLoginOverThresholdEvent(fingerprint));
        }
    }
}
